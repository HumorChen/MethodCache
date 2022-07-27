package cn.humorchen.methodcache.storage;


import cn.humorchen.methodcache.MethodCache;

import java.util.Collection;
import java.util.Map;

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

    /**
     * 获取所有key和过期时间
     * @return
     */
    Map<String,Long> getAllTtl();

    /**
     * 删除key
     * @param key
     */
    void remove(String key);

    /**
     * 删除key
     * @param keys
     */
    void remove(Collection<String> keys);
}
