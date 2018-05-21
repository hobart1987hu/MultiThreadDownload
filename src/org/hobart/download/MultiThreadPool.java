package org.hobart.download;

import java.util.Locale;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 多线程池
 */
public class MultiThreadPool {

    private static ExecutorService mThreadPool;

    /**
     * 线程池的基本大小
     */
    static int corePoolSize = 10;

    /**
     * 线程池最大数量
     */
    static int maximumPoolSizeSize = 100;
    /**
     * 线程活动保持时间
     */
    static long keepAliveTime = 60;

    private static LinkedBlockingQueue<Runnable> mQueue = new LinkedBlockingQueue<>();

    static {
        mThreadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSizeSize, keepAliveTime, TimeUnit.SECONDS, mQueue, new NamedThreadFactory());
    }

    private static class NamedThreadFactory implements ThreadFactory {

        private final AtomicInteger threadNumberAtomicInteger = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, String.format(Locale.CHINA, "%s%d", "NamedThreadFactory", threadNumberAtomicInteger.getAndIncrement()));
            thread.setPriority(Thread.NORM_PRIORITY);
            System.out.println("NamedThreadFactory thread name:" + thread.getName());
            return thread;
        }
    }

    public static void execute(Runnable thread) {
        mThreadPool.execute(thread);
    }
}
