package com.nanqing.rpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import com.nanqing.rpc.RpcApplication;
import com.nanqing.rpc.config.RpcConfig;
import com.nanqing.rpc.constant.RpcConstant;
import com.nanqing.rpc.model.RpcRequest;
import com.nanqing.rpc.model.RpcResponse;
import com.nanqing.rpc.model.ServiceMetaInfo;
import com.nanqing.rpc.protocol.*;
import com.nanqing.rpc.registry.Registry;
import com.nanqing.rpc.registry.RegistryFactory;
import com.nanqing.rpc.serializer.Serializer;
import com.nanqing.rpc.serializer.SerializerFactory;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ServiceProxy implements InvocationHandler {

    private static final Logger log = LoggerFactory.getLogger(ServiceProxy.class);

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 从配置读取序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        // 构造请求
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();

        try {
            // 序列化
            byte[] bodyBytes = serializer.serialize(rpcRequest);

            // 从注册中心获取服务提供者的地址
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = ServiceMetaInfo.builder()
                    .serviceName(serviceName)
                    .serviceVersion(RpcConstant.DEFAULT_SERVICE_VERSION)
                    .build();
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceMetaInfoList)) {
                throw new RuntimeException("暂无无服务地址");
            }
            ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfoList.get(0);

            // 发出 TCP 请求
            Vertx vertx = Vertx.vertx();
            NetClient netClient = vertx.createNetClient();
            CompletableFuture<RpcResponse> rpcResponseFuture = new CompletableFuture<>();
            netClient.connect(selectedServiceMetaInfo.getServicePort(), selectedServiceMetaInfo.getServiceHost(),
                    res -> {
                        if (res.succeeded()) {
                            log.info("Connected to TCP server");
                            io.vertx.core.net.NetSocket socket = res.result();
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
                            socket.handler(buffer -> {
                                try {
                                    ProtocolMessage<RpcResponse> rpcResponseProtocolMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                                    rpcResponseFuture.complete(rpcResponseProtocolMessage.getBody());
                                } catch (IOException e) {
                                    throw new RuntimeException("协议消息解码错误");
                                }
                            });
                        } else {
                            log.error("Failed to connect to TCP server");
                        }
                    });

            RpcResponse rpcResponse = rpcResponseFuture.get();
            netClient.close();
            return rpcResponse.getData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
