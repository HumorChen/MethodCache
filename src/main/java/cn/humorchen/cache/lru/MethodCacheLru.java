package cn.humorchen.cache.lru;

/**
 * 方法缓存淘汰算法接口
 * @author humorchen
 * @date 2022/7/25
 */
public interface MethodCacheLru {
    /**
     * 使用记录
     * @param key
     */
    void record(String key);

    /**
     * 写入一个key
     * 无需淘汰其他key则返回null
     * 需要淘汰某个key则返回对应key
     * @param key
     * @return
     */
    String write(String key);
}
