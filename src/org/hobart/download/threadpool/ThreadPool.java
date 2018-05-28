package org.hobart.download.threadpool;

public interface ThreadPool {

    void execute(Runnable task);

    void shutDown();
}
