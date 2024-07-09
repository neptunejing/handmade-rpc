package com.nanqing.minirpc.serializer;

import java.io.*;

public class JdkSerializer implements Serializer{

    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        // 创建字节数组输出流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // 创建 ObjectOutputStream 实例包装在 outputStream 上，用于将对象写入字节流
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        // 写入对象
        objectOutputStream.writeObject(obj);
        objectOutputStream.close();
        // 获取序列化后的字节数组
        return outputStream.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        // 创建 ByteArrayInputStream 实例，包装字节数组 bytes
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        // 创建 ObjectInputStream 实例，包装 inputStream
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        try {
            // 读取对象，强制转换为 T 类型对象
            return (T) objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            objectInputStream.close();
        }
    }
}
