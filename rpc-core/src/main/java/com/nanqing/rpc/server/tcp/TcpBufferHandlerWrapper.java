package com.nanqing.rpc.server.tcp;

import com.nanqing.rpc.protocol.ProtocolConstant;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;

public class TcpBufferHandlerWrapper implements Handler<Buffer> {
    private final RecordParser recordParser;

    public TcpBufferHandlerWrapper(Handler<Buffer> handler) {
        recordParser = initRecordParser(handler);
    }

    @Override
    public void handle(Buffer buffer) {
        recordParser.handle(buffer);
    }

    private RecordParser initRecordParser(Handler<Buffer> handler) {
        // 构造 parser
        RecordParser parser = RecordParser.newFixed(ProtocolConstant.MESSAGE_HEADER_LENGTH);

        parser.setOutput(new Handler<Buffer>() {
            // 初始化
            int size = -1;
            // 一次完整的读取（header + body）
            Buffer resultBuffer = Buffer.buffer();

            // 增强逻辑
            @Override
            public void handle(Buffer buffer) {
                if (size == -1) {
                    // bodyLength 是 header 最后的一个 int（前面有 5 * byte + long = 13 bytes）
                    size = buffer.getInt(13);
                    parser.fixedSizeMode(size);
                    // 先写入 header
                    resultBuffer.appendBuffer(buffer);
                } else {
                    // 写入 body
                    resultBuffer.appendBuffer(buffer);
                    // 写入完整的消息，可以开始处理
                    handler.handle(resultBuffer);
                    // 重置
                    parser.fixedSizeMode(ProtocolConstant.MESSAGE_HEADER_LENGTH);
                    size = -1;
                    resultBuffer = Buffer.buffer();
                }
            }
        });

        return parser;
    }
}
