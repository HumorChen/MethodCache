package cn.humorchen.cache.enhance;

import cn.humorchen.cache.MethodCache;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 计数
 *
 * @author humorchen
 * @date 2022/7/25 15:48
 */
public class MethodCacheCountEnhancer implements MethodCacheEnhancer {
    /**
     * 初始大小
     */
    private final int INITIAL_SIZE = 1000;
    /**
     * 计数
     */
    private final Map<String, AtomicLong> countMap = new HashMap<>(INITIAL_SIZE);
    /**
     * 缓存掉的个数
     */
    private final Map<String, AtomicLong> cachedMap = new HashMap<>(INITIAL_SIZE);
    /**
     * 开始时间
     */
    private final Map<String, Long> startTimeMap = new HashMap<>(INITIAL_SIZE);
    /**
     * 花费的时间
     */
    private final Map<String, AtomicLong> usedTimeMap = new HashMap<>(INITIAL_SIZE);

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
    @Override
    public void before(String id, MethodCache methodCache, String key, long currentTime,Class<?> cls, MethodSignature methodSignature, Object... args) {
        String methodName = getMethodName(cls,methodSignature);
        AtomicLong count = countMap.computeIfAbsent(methodName, (k) -> new AtomicLong());
        count.incrementAndGet();

        startTimeMap.put(id, currentTime);
    }

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
    @Override
    public void after(String id, MethodCache methodCache, String key, long currentTime,Class<?> cls, MethodSignature methodSignature, boolean cached,
                      Object... args) {
        String methodName = getMethodName(cls,methodSignature);
        Long startTime = startTimeMap.remove(id);
        if (startTime != null) {
            AtomicLong time = usedTimeMap.computeIfAbsent(methodName, (k) -> new AtomicLong());
            time.addAndGet(currentTime - startTime);
        }

        if (cached) {
            AtomicLong cachedCount = cachedMap.computeIfAbsent(methodName, (k) -> new AtomicLong());
            cachedCount.incrementAndGet();
        }
    }

    public Map<String, AtomicLong> getCountMap() {
        return countMap;
    }

    public Map<String, AtomicLong> getCachedMap() {
        return cachedMap;
    }

    public Map<String, Long> getStartTimeMap() {
        return startTimeMap;
    }

    public Map<String, AtomicLong> getUsedTimeMap() {
        return usedTimeMap;
    }

    /**
     * 获取方法名字
     * @param cls
     * @param methodSignature
     * @return
     */
    private String getMethodName(Class<?> cls,MethodSignature methodSignature) {
        return cls.getName() + "#" + methodSignature.getMethod().getName();
    }

    /**
     * 打印下情况
     */
    public void print() {
        countMap.forEach((methodName, count) -> {
            long c = count.get();
            long cached = cachedMap.get(methodName).get();
            long usedTime = usedTimeMap.get(methodName).get();
            String avg = BigDecimal.valueOf(usedTime).divide(BigDecimal.valueOf(c)).setScale(10).toString();
            System.out.println("method " + methodName + " invoke times is " + count + " cached times is " + cached + " used time "+usedTime+" ms,avg is " + avg);
        });
    }
}
