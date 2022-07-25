package cn.humorchen.cache.enhance;

import cn.humorchen.cache.MethodCache;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * 方法缓存增强（统计命中率、访问量之类的）
 *
 * @author humorchen
 * @date 2022/7/25
 */
public interface MethodCacheEnhancer {
    /**
     * 处理前
     *
     * @param id
     * @param methodCache
     * @param key
     * @param currentTime
     * @param cls
     * @param methodSignature
     * @param args
     */
    void before(String id, MethodCache methodCache, String key, long currentTime,Class<?> cls, MethodSignature methodSignature, Object... args);

    /**
     * 处理后
     *
     * @param id
     * @param methodCache
     * @param key
     * @param currentTime
     * @param cls
     * @param methodSignature
     * @param cached
     * @param args
     */
    void after(String id, MethodCache methodCache, String key, long currentTime,Class<?> cls, MethodSignature methodSignature, boolean cached, Object... args);
}
