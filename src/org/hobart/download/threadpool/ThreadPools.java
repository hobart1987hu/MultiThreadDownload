package org.hobart.download.threadpool;

import org.hobart.download.threadpool.impl.ThreadPoolImpl;

import java.util.concurrent.atomic.AtomicInteger;

public final class ThreadPools {


    public static ThreadPool newFixedThreadPool(int maxPoolSize) {
        ThreadPoolImpl impl = new ThreadPoolImpl(maxPoolSize, defaultThreadFactory());
        return impl;
    }

    public static ThreadFactory defaultThreadFactory() {
        return defaultThreadFactory("multi-download-pool-");
    }


    public static ThreadFactory defaultThreadFactory(String prefix) {
        return new DefaultThreadFactory(prefix);
    }


    public interface ThreadFactory {
        public Thread newThread(Runnable r);
    }

    static class DefaultThreadFactory implements ThreadFactory {

        private String prefix;

        private ThreadGroup g;

        private final AtomicInteger threadNumber = new AtomicInteger(0);

        private static final AtomicInteger poolCounter = new AtomicInteger(0);

        public DefaultThreadFactory(String prefix) {
            this.prefix = prefix + "_" + poolCounter.incrementAndGet();
            g = Thread.currentThread().getThreadGroup();
        }

        @Override
        public Thread newThread(Runnable r) {

            Thread t = new Thread(g, r, this.prefix + "_" + threadNumber.incrementAndGet());

            if (t.isDaemon()) {
                t.setDaemon(false);
            }

            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }

            return t;
        }
    }
}
