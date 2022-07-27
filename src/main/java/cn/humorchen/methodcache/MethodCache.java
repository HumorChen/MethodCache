package cn.humorchen.methodcache;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 方法缓存
 *
 * @author humorchen
 * @date 2022/7/22
 */
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
