package cn.humorchen.methodcache.log;


import cn.humorchen.methodcache.error.MethodCacheLogQueueFullFilledException;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 默认异步队列日志记录器（可继承该类重写write）
 *
 * @author humorchen
 * @date 2022/7/22 18:28
 */
public class DefaultAsyncQueueMethodCacheLogger implements MethodCacheLogger {
    /**
     * 最小处理级别
     */
    private MethodCacheLogLevel minLevel = MethodCacheLogLevel.INFO;
    /**
     * 分隔符
     */
    private final String DELIMITER = "\n";
    /**
     * 未锁定的值
     */
    private final int UNLOCKED = 0;
    /**
     * 日志定时刷
     */
    private final int LOG_WRITE_DELAY = 1000;
    /**
     * 日志一批写入容量
     */
    private final int QUEUE_BATCH_SIZE = 1000;
    /**
     * 日志最大容量
     */
    private final int QUEUE_MAX_SIZE = 1000000;
    /**
     * 随机数
     */
    private final Random random = new Random();

    /**
     * 刷日志的锁
     */
    private final AtomicInteger atomicInteger = new AtomicInteger(UNLOCKED);
    /**
     * 线程池
     */
    private final ScheduledExecutorService executors = Executors.newScheduledThreadPool(2);
    /**
     * 日志队列
     */
    private Map<MethodCacheLogLevel, LinkedList<String>> logQueueMap =
            Arrays.stream(MethodCacheLogLevel.values()).collect(Collectors.toMap(logLevel -> logLevel, logLevel -> new LinkedList<String>()));

    public DefaultAsyncQueueMethodCacheLogger() {
        executors.scheduleWithFixedDelay(this::triggerLogWrite, LOG_WRITE_DELAY, LOG_WRITE_DELAY, TimeUnit.MILLISECONDS);
    }

    /**
     * 记录
     *
     * @param msg
     */
    @Override
    public void debug(String msg) {
        normalLog(MethodCacheLogLevel.DEBUG, msg);
    }

    /**
     * 记录
     *
     * @param msg
     */
    @Override
    public void info(String msg) {
        normalLog(MethodCacheLogLevel.INFO, msg);
    }

    /**
     * 记录
     *
     * @param msg
     */
    @Override
    public void warn(String msg) {
        normalLog(MethodCacheLogLevel.WARN, msg);
    }

    /**
     * 处理异常
     *
     * @param msg
     * @param e
     */
    @Override
    public void error(String msg, Throwable e) {
        MethodCacheLogLevel level = MethodCacheLogLevel.ERROR;
        LinkedList<String> queue = logQueueMap.get(level);
        int size = queue.size();

        if (size < QUEUE_MAX_SIZE) {
            String log =
                    level.getDesc() + msg + DELIMITER + e.getMessage() + DELIMITER + Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.joining(DELIMITER));
            queue.add(log);
        } else {
            throw new MethodCacheLogQueueFullFilledException(level, size);
        }

        if (size > QUEUE_BATCH_SIZE) {
            executors.execute(this::triggerLogWrite);
        }
    }

    /**
     * 普通记录
     *
     * @param level
     * @param msg
     */
    private void normalLog(MethodCacheLogLevel level, String msg) {
        if (level.getId() < minLevel.getId()) {
            return;
        }
        LinkedList<String> queue = logQueueMap.get(level);
        int size = queue.size();

        if (size < QUEUE_MAX_SIZE) {
            String log = level.getDesc() + msg;
            queue.add(log);
        } else {
            throw new MethodCacheLogQueueFullFilledException(level, size);
        }

        if (size > QUEUE_BATCH_SIZE) {
            executors.execute(this::triggerLogWrite);
        }
    }

    /**
     * 写入日志（可以重写掉）
     *
     * @param logLevel
     * @param logQueue
     * @param length
     */
    @Override
    public void batchFlushLog(MethodCacheLogLevel logLevel, Queue<String> logQueue, int length) {
        while (length-- > 0) {
            String poll = logQueue.poll();
            if (poll == null) {
                return;
            }
            System.out.println(poll);
        }
    }

    /**
     * 触发写入日志
     */
    private void triggerLogWrite() {
        int randomNum = random.nextInt(Integer.MAX_VALUE);
        boolean locked = atomicInteger.compareAndSet(UNLOCKED, randomNum);
        try {
            if (locked) {
                logQueueMap.forEach((logLevel, logQueue) -> {
                    if (logQueue.size() > QUEUE_BATCH_SIZE) {
                        batchFlushLog(logLevel, logQueue, logQueue.size());
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (locked) {
                atomicInteger.compareAndSet(randomNum, UNLOCKED);
            }
        }
    }

    public MethodCacheLogLevel getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(MethodCacheLogLevel minLevel) {
        this.minLevel = minLevel;
    }
}
