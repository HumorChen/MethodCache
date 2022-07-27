package cn.humorchen.methodcache.storage;


import cn.humorchen.methodcache.MethodCache;
import cn.humorchen.methodcache.serialize.MethodCacheArgumentSerializer;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;

/**
 * 默认的存储key生成器
 *
 * @author humorchen
 * @date 2022/7/25 12:09
 */
public class DefaultMethodCacheStorageKeyGenerator implements MethodCacheStorageKeyGenerator {
    private MethodCacheArgumentSerializer methodCacheArgumentSerializer;

    @Autowired
    public DefaultMethodCacheStorageKeyGenerator(MethodCacheArgumentSerializer methodCacheArgumentSerializer) {
        this.methodCacheArgumentSerializer = methodCacheArgumentSerializer;
    }

    /**
     * 生成存储key
     *
     * @param methodCache
     * @param cls
     * @param method
     * @param args
     * @return
     */
    @Override
    public String generate(MethodCache methodCache, Class<?> cls, Method method, Object... args) {
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(cls.getName());
        keyBuilder.append("#");
        keyBuilder.append(method.getName());
        if (methodCache.identifyArguments()) {
            keyBuilder.append("(");
            // 区分参数
            int[] identifyIncludeArguments = methodCache.identifyIncludeArguments();
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (identifyIncludeArguments.length > 0) {
                // 只辨别需要的参数
                for (int i : identifyIncludeArguments) {
                    addArgKey(keyBuilder, i, parameterTypes[i - 1], args[i - 1]);
                }
            } else {
                // 辨别所有参数
                for (int i = 0; i < parameterTypes.length; i++) {
                    addArgKey(keyBuilder, i + 1, parameterTypes[i], args[i]);
                }
            }
            keyBuilder.append(")");
        }
        return keyBuilder.toString();
    }

    /**
     * 添加参数到key
     *
     * @param keyBuilder
     * @param index
     * @param argType
     * @param arg
     */
    private void addArgKey(StringBuilder keyBuilder, int index, Class argType, Object arg) {
        if (index > 1) {
            keyBuilder.append(",");
        }
        // 下标
        keyBuilder.append("arg");
        keyBuilder.append(index);
        keyBuilder.append('=');
        // 加入参数识别
        if (argType.isPrimitive()) {
            // 基本数据类型直接加上
            keyBuilder.append(arg);
        } else if (arg instanceof MethodCacheArgumentSerializer) {
            // 实现了参数key生成器
            keyBuilder.append(((MethodCacheArgumentSerializer) arg).serialize());
        } else {
            // 默认生成方式
            keyBuilder.append(methodCacheArgumentSerializer.serialize(arg));
        }
    }
}
