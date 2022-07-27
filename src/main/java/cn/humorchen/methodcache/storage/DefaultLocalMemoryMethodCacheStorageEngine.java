package cn.humorchen.methodcache.storage;


import cn.humorchen.methodcache.MethodCache;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

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
     * 使用并行的阈值
     */
    private final int PARALLEL_THRESHOLD = 10000;
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
     * 移除元素
     */
    private Consumer<String> consumer = (key) -> {
        expireMap.remove(key);
        retMap.remove(key);
    };

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

    /**
     * 获取所有key和过期时间
     *
     * @return
     */
    @Override
    public Map<String, Long> getAllTtl() {
        return expireMap;
    }

    /**
     * 删除key
     *
     * @param key
     */
    @Override
    public void remove(String key) {
        consumer.accept(key);
    }

    /**
     * 删除key
     *
     * @param keys
     */
    @Override
    public void remove(Collection<String> keys) {
        if (keys == null){
            return;
        }
        if (keys.size() < PARALLEL_THRESHOLD){
            keys.forEach(consumer);
        }else {
            keys.stream().parallel().forEach(consumer);
        }
    }
}
