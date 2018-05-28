package org.hobart.download;

import org.hobart.download.threadpool.ThreadPool;
import org.hobart.download.threadpool.ThreadPools;

/**
 * 多线程池
 */
public class MultiThreadPool {

    private static ThreadPool mThreadPool = ThreadPools.newFixedThreadPool(5);

    public static void execute(Runnable thread) {
        mThreadPool.execute(thread);
    }

    public static void shutDownThreadPool() {
        mThreadPool.shutDown();
    }
}
