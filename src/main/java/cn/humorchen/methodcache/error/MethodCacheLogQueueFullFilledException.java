package cn.humorchen.methodcache.error;


import cn.humorchen.methodcache.log.MethodCacheLogLevel;

/**
 * 日志队列满了
 *
 * @author humorchen
 * @date 2022/7/25 12:48
 */
public class MethodCacheLogQueueFullFilledException extends RuntimeException {
    /**
     * 日志级别
     */
    private MethodCacheLogLevel logLevel;
    /**
     * 队列当前大小
     */
    private int queueSize;

    public MethodCacheLogQueueFullFilledException(MethodCacheLogLevel logLevel, int queueSize) {
        this.logLevel = logLevel;
        this.queueSize = queueSize;
    }

    @Override
    public String getMessage() {
        return "MethodCache log queue " + logLevel.getLevel() + " is full filled,now queue size is " + queueSize;
    }
}
