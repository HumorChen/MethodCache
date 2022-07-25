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
     * @param id 调用ID（uuid）
     * @param methodCache 方法上的缓存注解
     * @param key 本次调用缓存的存储key
     * @param currentTime 当前时间
     * @param cls 被调用方法所在类
     * @param methodSignature 方法声明
     * @param args 方法调用参数
     */
    void before(String id, MethodCache methodCache, String key, long currentTime,Class<?> cls, MethodSignature methodSignature, Object... args);

    /**
     * 处理后
     *
     * @param id 调用ID（uuid）
     * @param methodCache 方法上的缓存注解
     * @param key 本次调用缓存的存储key
     * @param currentTime 当前时间
     * @param cls 被调用方法所在类
     * @param methodSignature 方法声明
     * @param cached 本次调用是否命中缓存
     * @param args 方法调用参数
     */
    void after(String id, MethodCache methodCache, String key, long currentTime,Class<?> cls, MethodSignature methodSignature, boolean cached, Object... args);
}
