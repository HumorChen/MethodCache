package cn.humorchen.cache.storage;


import cn.humorchen.cache.MethodCache;

import java.util.HashMap;
import java.util.Map;

/**
 * 默认本地内存存储引擎
 *
 * @author humorchen
 * @date 2022/7/25 11:07
 */
public class DefaultLocalMemoryMethodCacheStorageEngine implements MethodCacheStorageEngine {
    /**
     * 容器初始化大小
     */
    private final int INITIAL_CONTAINER_SIZE = 1024;
    /**
     * 装返回值
     * key->return object
     */
    private final Map<String, Object> retMap = new HashMap<>(INITIAL_CONTAINER_SIZE);
    /**
     * 装过期时间
     * key -> next proceed time mills
     */
    private Map<String, Long> expireMap = new HashMap<>(INITIAL_CONTAINER_SIZE);


    /**
     * 读取
     *
     * @param key
     * @return
     */
    @Override
    public Object read(String key) {
        return retMap.get(key);
    }

    /**
     * 写入
     *
     * @param methodCache
     * @param key
     * @param value
     */
    @Override
    public void write(MethodCache methodCache, String key, Object value) {
        retMap.put(key, value);
        expireMap.put(key, System.currentTimeMillis() + methodCache.unit().toMillis(methodCache.time()));
    }

    /**
     * 获取key剩下的存活时间
     *
     * @param key
     * @return
     */
    @Override
    public Long ttl(String key) {
        Long expire = expireMap.get(key);
        return expire == null ? null : expire - System.currentTimeMillis();
    }
}
