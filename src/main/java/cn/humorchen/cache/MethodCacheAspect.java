package cn.humorchen.cache;


import cn.humorchen.cache.enhance.MethodCacheEnhancer;
import cn.humorchen.cache.log.MethodCacheLogger;
import cn.humorchen.cache.storage.MethodCacheStorageEngine;
import cn.humorchen.cache.storage.MethodCacheStorageKeyGenerator;
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

    @Autowired
    public MethodCacheAspect(MethodCacheLogger logger, MethodCacheStorageEngine storageEngine, MethodCacheStorageKeyGenerator methodCacheStorageKeyGenerator,
                             List<MethodCacheEnhancer> enhancerChain) {
        this.logger = logger;
        this.storageEngine = storageEngine;
        this.methodCacheStorageKeyGenerator = methodCacheStorageKeyGenerator;
        this.enhancerChain = enhancerChain;
    }


    @Around("@annotation(cn.humorchen.cache.MethodCache)")
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
        try {
            // 增强链
            enhancerChain.forEach(methodCacheEnhancer -> methodCacheEnhancer.before(id, methodCache, key, System.currentTimeMillis(),cls, methodSignature,
                    args));
            // 过期时间
            Long ttl = storageEngine.ttl(key);
            if (ttl == null || ttl < 1) {
                // 没有结果
                ret = proceed(proceedingJoinPoint);
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
                cached = true;
                // 日志
                logger.debug(key + " used cache");
            }
            // 增强链
            for (MethodCacheEnhancer enhancer : enhancerChain) {
                enhancer.after(id, methodCache, key, System.currentTimeMillis(),cls, methodSignature, cached, args);
            }
        } catch (Exception e) {
            logger.error("MethodCache error", e);
            for (MethodCacheEnhancer enhancer : enhancerChain) {
                enhancer.after(id, methodCache, key, System.currentTimeMillis(),cls, methodSignature, cached, args);
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
