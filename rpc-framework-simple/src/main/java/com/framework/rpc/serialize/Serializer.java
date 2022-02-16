package com.framework.rpc.serialize;

public interface Serializer {

    /**
     * 序列化
     *
     * @param obj
     * @return
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化
     *
     * @param bytes 序列化后的字节数组
     * @param clazz 类
     * @param <T>
     * @return 反序列化得到的对象
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
