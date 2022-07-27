package cn.humorchen.methodcache;


import cn.humorchen.methodcache.enhance.MethodCacheEnhancer;
import cn.humorchen.methodcache.log.MethodCacheLogger;
import cn.humorchen.methodcache.lru.MethodCacheKeyManager;
import cn.humorchen.methodcache.storage.MethodCacheStorageEngine;
import cn.humorchen.methodcache.storage.MethodCacheStorageKeyGenerator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

/**
 * 方法缓存切面
 *
 * @author humorchen
 * @date 2022/7/22 16:23
 */
@Aspect
@Component
public class MethodCacheAspect {
    /**
     * 存储引擎
     */
    private MethodCacheStorageEngine storageEngine;
    /**
     * 存储key生成器
     */
    private MethodCacheStorageKeyGenerator methodCacheStorageKeyGenerator;
    /**
     * 日志
     */
    private MethodCacheLogger logger;
    /**
     * 增强链
     */
    private List<MethodCacheEnhancer> enhancerChain;
    /**
     * 缓存键管理器
     */
    private MethodCacheKeyManager keyManager;


    @Autowired
    public MethodCacheAspect(MethodCacheLogger logger, MethodCacheStorageEngine storageEngine, MethodCacheStorageKeyGenerator methodCacheStorageKeyGenerator,
                             List<MethodCacheEnhancer> enhancerChain,MethodCacheKeyManager keyManager) {
        this.logger = logger;
        this.storageEngine = storageEngine;
        this.methodCacheStorageKeyGenerator = methodCacheStorageKeyGenerator;
        this.enhancerChain = enhancerChain;
        this.keyManager = keyManager;
    }


    @Around("@annotation(cn.humorchen.methodcache.MethodCache)")
    public Object aroundMethodCache(ProceedingJoinPoint proceedingJoinPoint) {
        Object ret = null;
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();
        // 返回值是void直接执行返回
        if (method.getReturnType().equals(Void.class)) {
            return proceed(proceedingJoinPoint);
        }
        MethodCache methodCache = method.getAnnotation(MethodCache.class);
        Object[] args = proceedingJoinPoint.getArgs();
        //被代理的类
        Class<?> cls = proceedingJoinPoint.getTarget().getClass();
        // 生成key
        String key = methodCacheStorageKeyGenerator.generate(methodCache,cls , method, args);
        // 本次的调用id
        String id = UUID.randomUUID().toString();
        boolean cached = false;
        // 键管理器写入有可能要淘汰某个key
        String removedKey = null;
        try {
            // 增强链
            enhancerChain.forEach(methodCacheEnhancer -> methodCacheEnhancer.before(id, methodCache, key, System.currentTimeMillis(),cls, methodSignature,
                    args));
            // 过期时间
            Long ttl = storageEngine.ttl(key);
            if (ttl == null || ttl < 1) {
                // 没有结果
                ret = proceed(proceedingJoinPoint);
                // 写入键
                removedKey = keyManager.write(key);
                // 存储结果
                storageEngine.write(methodCache, key, ret);
                // 日志
                if (ttl == null) {
                    logger.debug(key + " no cache existed,cached after this time.");
                } else {
                    logger.debug(key + " cache expired,proceed method and cached now.");
                }
            } else {
                // 读取缓存结果
                ret = storageEngine.read(key);
                // 记录使用过该缓存
                keyManager.record(key);
                // 标记被缓存
                cached = true;
                // 日志
                logger.debug(key + " used cache");
            }
            if (removedKey != null){
                // 删除缓存
                storageEngine.remove(removedKey);
            }
            // 增强链
            for (MethodCacheEnhancer enhancer : enhancerChain) {
                enhancer.after(id, methodCache, key, System.currentTimeMillis(),cls, methodSignature, cached,ret,removedKey, args);
            }
        } catch (Exception e) {
            logger.error("MethodCache error", e);
            for (MethodCacheEnhancer enhancer : enhancerChain) {
                enhancer.after(id, methodCache, key, System.currentTimeMillis(),cls, methodSignature, cached,ret,removedKey, args);
            }
        }

        return ret;
    }


    /**
     * 执行返回对象
     *
     * @param proceedingJoinPoint
     * @return
     */
    private Object proceed(ProceedingJoinPoint proceedingJoinPoint) {
        try {
            return proceedingJoinPoint.proceed();
        } catch (Throwable e) {
            logger.error("proceed method error", e);
        }
        return null;
    }


}
