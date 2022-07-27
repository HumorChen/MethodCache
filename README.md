## MethodCache方法级缓存框架
### 第一眼示范

```java
import cn.humorchen.methodcache.MethodCache;
import org.springframework.stereotype.Component;

@Component
public class Dog {
    /**
     * 此方法会被缓存掉，当缓存存在并有效时不会执行方法，而是直接返回结果
     *
     * @return
     */
    @MethodCache
    public String say() {
        return "汪汪汪";
    }
}
```

### 注解定义
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodCache {

    /**
     * 有效时间（默认2秒）
     *
     * @return
     */
    int time() default 2;

    /**
     * 时间单位（默认秒）
     *
     * @return
     */
    TimeUnit unit() default TimeUnit.SECONDS;

    /**
     * 区分参数（未设置参数名则区分所有参数）
     *
     * @return
     */
    boolean identifyArguments() default false;

    /**
     * 要区分的参数下标（从1开始,参数非基本数据类型实现了MethodCacheArgumentKeyGenerator接口则调用你自定义key实现）
     *
     * @return
     */
    int[] identifyIncludeArguments() default {};
}

```
### 一些示范

```java

import cn.humorchen.methodcache.MethodCache;
import cn.humorchen.methodcache.serialize.DefaultMethodCacheArgumentSerializer;
import cn.humorchen.methodcache.serialize.MethodCacheArgumentSerializer;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class Dog implements MethodCacheArgumentSerializer {
    public String say() {
        return "狗子：汪汪汪" + "( " + new Date() + " )";
    }

    /**
     * 直接缓存该方法，缓存有效时间2秒
     * @return
     */
    @MethodCache
    public String say1() {
        return say();
    }

    /**
     * 直接缓存该方法，缓存有效时间10秒
     * @return
     */
    @MethodCache(time = 10)
    public String say2() {
        return say();
    }

    /**
     * 直接缓存该方法，缓存有效时间10分钟
     * @return
     */
    @MethodCache(time = 10, unit = TimeUnit.MINUTES)
    public String say3() {
        return say();
    }


    /**
     * 直接缓存该方法，区分全部参数，当不同name传入缓存结果是不同的,缓存Key会是方法+参数值
     * @see DefaultMethodCacheArgumentSerializer
     * @param name
     * @return
     */
    @MethodCache(identifyArguments = true)
    public String say4(String name) {
        return say();
    }

    /**
     * 直接缓存该方法，且区分指定参数，当不同参数传入缓存结果是不同的,缓存Key会是方法+参数值
     * @see DefaultMethodCacheArgumentSerializer
     * @param name
     * @param age
     * @return
     */
    @MethodCache(identifyArguments = true, identifyIncludeArguments = {1, 2})
    public String say5(String name, int age) {
        return say();
    }


    /**
     * 直接缓存该方法，且区分参数，参数序列化由你自己实现，当不同参数传入缓存结果是不同的,缓存Key会是方法+参数值
     * @see MethodCacheArgumentSerializer
     * @param dog
     * @return
     */
    @MethodCache(identifyArguments = true)
    public String say6(Dog dog) {
        return say();
    }


    /**
     * 自定义参数序列化
     *
     * @return
     */
    @Override
    public String serialize() {
        return "my diy key";
    }
}
```
## 可自定义的部分
> 直接实现对应接口并注入IOC容器即可替换默认实现
### 存储引擎

```java

import cn.humorchen.methodcache.MethodCache;

/**
 * 方法缓存存储引擎
 *
 * @author humorchen
 * @date 2022/7/25
 */
public interface MethodCacheStorageEngine {

    /**
     * 读取（过期也要返回Null）
     *
     * @param key
     * @return
     */
    Object read(String key);

    /**
     * 写入
     *
     * @param methodCache
     * @param key
     * @param value
     */
    void write(MethodCache methodCache, String key, Object value);


    /**
     * 获取key剩下的存活时间
     *
     * @param key
     * @return
     */
    Long ttl(String key);
}
```

### 存储key生成器

```java

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

```


### 序列化器
> 当需要序列化结果为字符串放入redis等地方时，可以使用该序列化器去序列化方法执行结果，已提供一个默认的fastjson的序列化器实现

```java
/**
 * 方法缓存序列化器
 *
 * @author humorchen
 * @date 2022/7/25 11:28
 */
public interface MethodCacheSerializer {
    /**
     * 序列化为字符串
     *
     * @param obj
     * @return
     */
    String serialize(Object obj);

    /**
     * 反序列化为对象
     *
     * @param serializedString
     * @param cls
     * @return
     */
    Object deserialize(String serializedString, Class<?> cls);
}

```
### 方法参数的自定义序列化接口
> 当你的参数不是基本数据类型，而且不适合用json序列化来区分不同参数对应不同执行结果时，可以自行实现该参数序列化接口，即可使用你的方法进行序列化区分参数

```java

/**
 * 方法缓存参数序列化器（用于区分参数时自定义序列化参数）
 *
 * @author humorchen
 * @date 2022/7/22
 */
public interface MethodCacheArgumentSerializer {
    /**
     * 序列化参数
     *
     * @return
     */
    String serialize();

    /**
     * 序列化
     *
     * @param obj
     * @return
     */
    default String serialize(Object obj) {
        return JSONObject.toJSONString(obj);
    }
}

```

### 日志记录器
> 默认的日志记录器是一个异步队列式的，日志会根据对应日志级别进入对应的日志队列，定时刷出(1000毫秒刷出，且默认日志级别为ERROR,可自行配置)，默认刷出方式为控制台打印（可继承他并注入IOC实现替换）
> 若需要将日志刷出到你需要的位置去，可重写void batchFlushLog(MethodCacheLogLevel logLevel, Queue<String> logQueue, int length)该方法实现。
> 若不需要该日志记录器，可直接实现MethodCacheLogger并注入IOC容器即可

```java

/**
 * 方法缓存错误处理器
 *
 * @author humorchen
 * @date 2022/7/22
 */
public interface MethodCacheLogger {
    /**
     * 记录
     *
     * @param msg
     */
    void debug(String msg);

    /**
     * 记录
     *
     * @param msg
     */
    void info(String msg);

    /**
     * 记录
     *
     * @param msg
     */
    void warn(String msg);

    /**
     * 处理异常
     *
     * @param msg
     * @param e
     */
    void error(String msg, Throwable e);

    /**
     * 批量刷日志
     *
     * @param logLevel
     * @param logQueue
     * @param length
     */
    void batchFlushLog(MethodCacheLogLevel logLevel, Queue<String> logQueue, int length);


}

```
```java

/**
 * 日志级别
 *
 * @author humorchen
 * @date 2022/7/25
 */
public enum MethodCacheLogLevel {
    DEBUG(1, "debug", "MethodCache Debug "),
    INFO(2, "info", "MethodCache Info "),
    WARN(3, "warn", "MethodCache Warn "),
    ERROR(4, "error", "MethodCache Error ");

    private Integer id;
    private String level;
    private String desc;

    MethodCacheLogLevel(Integer id, String level, String desc) {
        this.id = id;
        this.level = level;
        this.desc = desc;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
```

### 能力增强
> 当你还需要一些其他功能时可实现增强接口(MethodCacheEnhancer)并注入IOC容器即可加入你的自定义增强功能
> 默认提供了一个方法的调用、缓存、耗时统计增强实现 MethodCacheCountEnhancer

```java

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

```

### 缓存淘汰算法
#### LRU实现
```java
/**
 * @see cn.humorchen.methodcache.lru.MethodCacheKeyLruManager
 */
/**
 * lru算法实现key管理
 * least recently use 最近未使用淘汰，使用了就放最前面
 * @author humorchen
 * @date 2022/7/26 20:40
 */
```