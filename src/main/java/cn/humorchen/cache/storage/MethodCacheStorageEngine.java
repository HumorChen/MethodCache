package cn.humorchen.cache.storage;


import cn.humorchen.cache.MethodCache;

/**
 * 方法缓存存储引擎
 *
 * @author humorchen
 * @date 2022/7/25
 */
public interface MethodCacheStorageEngine {

    /**
     * 读取（过期也要返回Null）
     *
     * @param key
     * @return
     */
    Object read(String key);

    /**
     * 写入
     *
     * @param methodCache
     * @param key
     * @param value
     */
    void write(MethodCache methodCache, String key, Object value);


    /**
     * 获取key剩下的存活时间
     *
     * @param key
     * @return
     */
    Long ttl(String key);
}
