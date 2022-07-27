package cn.humorchen.methodcache.storage;

import cn.humorchen.methodcache.MethodCache;

import java.lang.reflect.Method;

/**
 * 方法缓存存储key生成器
 *
 * @author humorchen
 * @date 2022/7/25
 */
public interface MethodCacheStorageKeyGenerator {
    /**
     * 生成存储key
     *
     * @param methodCache
     * @param cls
     * @param method
     * @param args
     * @return
     */
    String generate(MethodCache methodCache, Class<?> cls, Method method, Object... args);
}
