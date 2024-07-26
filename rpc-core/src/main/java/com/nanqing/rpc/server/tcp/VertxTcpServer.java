package com.nanqing.rpc.server.tcp;

import com.nanqing.rpc.server.HttpServer;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
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
            // 处理连接
            socket.handler(buffer -> {
                // 处理收到的字节数组
                byte[] requestData = buffer.getBytes();
                // 这里进行自定义字节数组梳理逻辑（解析请求、调用服务、构造响应）
                byte[] responseData = handleRequest(requestData);
                // 发送响应
                socket.write(Buffer.buffer(responseData));

            });
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


