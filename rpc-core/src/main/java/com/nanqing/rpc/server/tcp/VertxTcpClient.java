package com.nanqing.rpc.server.tcp;

import io.vertx.core.Vertx;
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
                // 发送数据
                socket.write("Hello, server!");
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
