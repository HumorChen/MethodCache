package cn.humorchen.methodcache.config;

import cn.humorchen.methodcache.enhance.MethodCacheCountEnhancer;
import cn.humorchen.methodcache.log.DefaultAsyncQueueMethodCacheLogger;
import cn.humorchen.methodcache.log.MethodCacheLogLevel;
import cn.humorchen.methodcache.log.MethodCacheLogger;
import cn.humorchen.methodcache.lru.MethodCacheKeyLruManager;
import cn.humorchen.methodcache.lru.MethodCacheKeyManager;
import cn.humorchen.methodcache.serialize.DefaultJsonMethodCacheSerializer;
import cn.humorchen.methodcache.serialize.DefaultMethodCacheArgumentSerializer;
import cn.humorchen.methodcache.serialize.MethodCacheArgumentSerializer;
import cn.humorchen.methodcache.serialize.MethodCacheSerializer;
import cn.humorchen.methodcache.storage.DefaultLocalMemoryMethodCacheStorageEngine;
import cn.humorchen.methodcache.storage.DefaultMethodCacheStorageKeyGenerator;
import cn.humorchen.methodcache.storage.MethodCacheStorageEngine;
import cn.humorchen.methodcache.storage.MethodCacheStorageKeyGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 方法缓存的配置
 *
 * @author humorchen
 * @date 2022/7/22 18:36
 */
@Configuration
public class MethodCacheConfiguration {
    /**
     * 默认参数序列化器
     */
    private MethodCacheArgumentSerializer defaultMethodCacheArgumentSerializer = new DefaultMethodCacheArgumentSerializer();



    /**
     * 注册默认的错误处理器（当没有自定义的logger的时候使用默认的）
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(MethodCacheLogger.class)
    public MethodCacheLogger registerDefaultMethodCacheLogger() {
        DefaultAsyncQueueMethodCacheLogger logger = new DefaultAsyncQueueMethodCacheLogger();
        logger.setMinLevel(MethodCacheLogLevel.ERROR);
        return logger;
    }

    /**
     * 注册默认基于本地内存的存储引擎
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(MethodCacheStorageEngine.class)
    public MethodCacheStorageEngine registerDefaultMethodCacheStorageEngine() {
        return new DefaultLocalMemoryMethodCacheStorageEngine();
    }

    /**
     * 注册默认的序列化器
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(MethodCacheSerializer.class)
    public MethodCacheSerializer registerDefaultMethodCacheSerializer() {
        return new DefaultJsonMethodCacheSerializer();
    }

    /**
     * 注册默认的参数序列化器
     *
     * @return
     */
    @Bean(DefaultMethodCacheArgumentSerializer.BEAN_NAME)
    public MethodCacheArgumentSerializer registerDefaultMethodCacheArgumentSerializer() {
        return defaultMethodCacheArgumentSerializer;
    }

    /**
     * 注册默认的参数序列化器
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(MethodCacheStorageKeyGenerator.class)
    public MethodCacheStorageKeyGenerator registerDefaultMethodCacheStorageKeyGenerator() {
        return new DefaultMethodCacheStorageKeyGenerator(defaultMethodCacheArgumentSerializer);
    }

    /**
     * 注册计数的增强器
     *
     * @return
     */
    @Bean
    public MethodCacheCountEnhancer registerCountEnhancer() {
        return new MethodCacheCountEnhancer();
    }


    /**
     * 注册默认的key管理器
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(MethodCacheKeyManager.class)
    public MethodCacheKeyManager registerMethodCacheKeyLRUManager(){
        return new MethodCacheKeyLruManager(65535);
    }

}
