package com.nanqing.rpc.server.tcp;

import com.nanqing.rpc.server.HttpServer;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.parsetools.RecordParser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VertxTcpServer implements HttpServer {
    private byte[] handleRequest(byte[] requestData) {
        // 根据 requestData 构造响应数据并返回
        // 此处是示例逻辑
        return "Hello, client!".getBytes();
    }

    @Override
    public void doStart(int port) {
        // 创建 Vert.x 实例
        Vertx vertx = Vertx.vertx();

        // 创建 TCP server
        NetServer server = vertx.createNetServer();

        // 处理请求
        server.connectHandler(socket -> {
            // 构造 parser
            RecordParser parser = RecordParser.newFixed(8); // header 长度为固定的 8 bytes
            parser.setOutput(new Handler<Buffer>() {
                // 初始化
                int size = -1;
                // 一次完整的读取（header + body）
                Buffer resultBuffer = Buffer.buffer();

                @Override
                public void handle(Buffer buffer) {
                    if (size == -1) {
                        size = buffer.getInt(4); // 先确认 body 长度
                        parser.fixedSizeMode(size); // 修改 parser 的固定长度
                        // 写入 header
                        resultBuffer.appendBuffer(buffer);
                    } else {
                        // 写入 body
                        resultBuffer.appendBuffer(buffer);
                        log.info(resultBuffer.toString());
                        // 重置一轮
                        parser.fixedSizeMode(8);
                        size = -1;
                        resultBuffer = Buffer.buffer();
                    }
                }
            });

            socket.handler(parser);
        });

        // 启动 TCP server 并监听端口
        server.listen(port, result -> {
            if (result.succeeded()) {
                log.info("TCP server started on port {}", port);
            } else {
                log.error("Failed to start TCP server", result.cause());
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpServer().doStart(8888);
    }
}


