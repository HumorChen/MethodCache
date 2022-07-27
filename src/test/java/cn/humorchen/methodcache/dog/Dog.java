package cn.humorchen.methodcache.dog;

import cn.humorchen.methodcache.MethodCache;
import cn.humorchen.methodcache.serialize.DefaultMethodCacheArgumentSerializer;
import cn.humorchen.methodcache.serialize.MethodCacheArgumentSerializer;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class Dog implements MethodCacheArgumentSerializer {
    public String say() {
        return "狗子：汪汪汪"+"( "+new Date()+" )";
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
    @MethodCache(time = 10,unit = TimeUnit.MINUTES)
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