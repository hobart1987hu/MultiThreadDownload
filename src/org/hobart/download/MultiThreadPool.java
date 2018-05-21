package org.hobart.download;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 多线程池
 */
public class MultiThreadPool {

    private static ExecutorService mExecutorService = Executors.newCachedThreadPool();

    public static void execute(Runnable thread) {
        mExecutorService.execute(thread);
    }
}
