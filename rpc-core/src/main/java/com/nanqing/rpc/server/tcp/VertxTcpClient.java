package com.nanqing.rpc.server.tcp;

import cn.hutool.core.util.IdUtil;
import com.nanqing.rpc.RpcApplication;
import com.nanqing.rpc.model.RpcRequest;
import com.nanqing.rpc.model.RpcResponse;
import com.nanqing.rpc.model.ServiceMetaInfo;
import com.nanqing.rpc.protocol.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Vertx TCP 请求客户端
 */
@Slf4j
public class VertxTcpClient {
    /**
     * 发送请求
     */
    public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) throws InterruptedException, ExecutionException {
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();
        CompletableFuture<RpcResponse> rpcResponseFuture = new CompletableFuture<>();
        netClient.connect(serviceMetaInfo.getServicePort(), serviceMetaInfo.getServiceHost(),
                res -> {
                    if (!res.succeeded()) {
                        log.error("connect failed", res.cause());
                        return;
                    }
                    NetSocket socket = res.result();

                    // 构造消息
                    ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
                    ProtocolMessage.Header header = new ProtocolMessage.Header();
                    header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                    header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                    header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
                    header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                    header.setRequestId(IdUtil.getSnowflakeNextId());
                    protocolMessage.setHeader(header);
                    protocolMessage.setBody(rpcRequest);

                    // 编码请求
                    try {
                        Buffer encodedBuffer = ProtocolMessageEncoder.encode(protocolMessage);
                        socket.write(encodedBuffer);
                    } catch (IOException e) {
                        throw new RuntimeException("协议消息编码错误");
                    }

                    // 接收响应
                    TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(
                            buffer -> {
                                try {
                                    ProtocolMessage<RpcResponse> rpcResponseProtocolMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                                    rpcResponseFuture.complete(rpcResponseProtocolMessage.getBody());
                                } catch (IOException e) {
                                    throw new RuntimeException("协议消息解码错误");
                                }
                            });

                    socket.handler(bufferHandlerWrapper);
                });

        RpcResponse rpcResponse = rpcResponseFuture.get();
        if (rpcResponse.getException() != null) {
            throw new RuntimeException("RPC 响应携带异常", rpcResponse.getException());
        }
        netClient.close();
        return rpcResponse;
    }
}
