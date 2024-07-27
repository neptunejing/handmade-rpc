package com.nanqing.rpc.server.tcp;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VertxTcpClient {
    public void start() {
        // 创建 Vert.x 实例
        Vertx vertx = Vertx.vertx();

        vertx.createNetClient().connect(8888, "localhost", res -> {
            if (res.succeeded()) {
                log.info("Connected to TCP Server");
                io.vertx.core.net.NetSocket socket = res.result();
                for (int i = 0; i < 1000; i++) {
                    // 发送数据
                    Buffer buffer = Buffer.buffer();
                    String str = "Hello, server!Hello, server!Hello, server!Hello, server!";
                    buffer.appendInt(0);
                    buffer.appendInt(str.getBytes().length); // 先写入 str 的字节长度（body 长度）
                    buffer.appendBytes(str.getBytes()); // 写入 str
                    socket.write(buffer);
                }
                // 接收响应
                socket.handler(buffer -> {
                    log.info("Received response from server: {}", buffer.toString());
                });
            } else {
                log.error("Failed to connect to TCP Server");
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpClient().start();
    }
}
