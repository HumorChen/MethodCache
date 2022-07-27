package cn.humorchen.methodcache.log;

import java.util.Queue;

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
