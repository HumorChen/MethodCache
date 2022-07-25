package cn.humorchen.cache.serialize;

/**
 * 方法缓存序列化器
 *
 * @author humorchen
 * @date 2022/7/25 11:28
 */
public interface MethodCacheSerializer {
    /**
     * 序列化为字符串
     *
     * @param obj
     * @return
     */
    String serialize(Object obj);

    /**
     * 反序列化为对象
     *
     * @param serializedString
     * @param cls
     * @return
     */
    Object deserialize(String serializedString, Class<?> cls);
}
