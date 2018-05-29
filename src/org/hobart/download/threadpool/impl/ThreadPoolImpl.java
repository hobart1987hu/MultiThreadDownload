package org.hobart.download.threadpool.impl;

import org.hobart.download.threadpool.ThreadPool;
import org.hobart.download.threadpool.ThreadPools;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolImpl implements ThreadPool {

    private final int corePoolSize;

    private final ThreadPools.ThreadFactory threadFactory;

    private final BlockingQueue<Runnable> queue;

    private final BlockingQueue<Worker> workers;

    private final AtomicInteger totalWorkers = new AtomicInteger(0);

    private static final int MAX_QUEUE_PRE_THREAD = 5;

    public ThreadPoolImpl(final int corePoolSize, final ThreadPools.ThreadFactory threadFactory) {
        this.corePoolSize = corePoolSize;
        this.threadFactory = threadFactory;
        this.workers = new LinkedBlockingQueue<>(corePoolSize);
        //队列可以保存的最大元素
        this.queue = new LinkedBlockingQueue<>(corePoolSize * MAX_QUEUE_PRE_THREAD);
    }

    /**
     * 代码逻辑：
     * 1、如果当前的workers 线程<=corePoolSize ,我们就开启新的线程去处理task
     * 2、如果当前的workers 线程>corePoolSize,那我们就把这个添加到队列里面去,等待线程执行完
     */

    @Override
    public void execute(Runnable task) {
        if (!addWorker(task)) {
            System.out.println("the workers is full ,so put the task to queue ");
            addToQueue(task);
        }
    }

    private void addToQueue(Runnable task) {
        try {
            this.queue.put(task);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean addWorker(Runnable task) {
        int workerCount = totalWorkers.get();
        boolean added = false;
        while (workerCount < this.corePoolSize) {
            added = this.totalWorkers.compareAndSet(workerCount, workerCount + 1);
            workerCount = totalWorkers.get();
            if (added) break;
        }
        if (added) {
            System.out.println("Worker added by " + Thread.currentThread().getName() + " " + " and worker count: " + workerCount);
            Worker worker = new Worker(task);
            this.workers.add(worker);
            worker.t.start();
        }
        return added;
    }

    @Override
    public void shutDown() {
        for (Worker w : workers) {
            w.setState(WorkerState.STOP);
            w.interruptMe();
        }
    }

    enum WorkerState {
        NEW, RUNNING, WAITING, STOP, DIRTY
    }

    class Worker implements Runnable {

        final Thread t;

        Runnable firstTask;

        volatile WorkerState state;

        public Worker(Runnable firstTask) {
            this.firstTask = firstTask;
            state = WorkerState.NEW;
            this.t = getThreadFactory().newThread(this);
        }

        @Override
        public void run() {
            runWorker(this);
        }

        private void runWorker(Worker w) {
            while (this.state != WorkerState.STOP && !t.isInterrupted()) {
                try {
                    System.out.println("runWorker thread name :" + this.t.getName());
                    Runnable firstTask = w.firstTask;
                    w.firstTask = null;
                    if (firstTask != null) {
                        setState(WorkerState.RUNNING);
                        firstTask.run();
                    }
                    setState(WorkerState.WAITING);
                    //waiting until next task is not  null
                    Runnable next = getNextTask();
                    System.out.println("worker thread execute next thread.");
                    w.firstTask = next;
                } catch (InterruptedException e) {
                    System.out.println("worker thread is Interrupted and current  total workers : " + totalWorkers.get());
                    setState(WorkerState.DIRTY);
                    Thread.currentThread().interrupt();
                }
            }
        }

        private Runnable getNextTask() throws InterruptedException {
            return ThreadPoolImpl.this.queue.take();
        }

        private ThreadPools.ThreadFactory getThreadFactory() {
            return ThreadPoolImpl.this.threadFactory;
        }

        private void setState(WorkerState state) {
            this.state = state;
        }

        void interruptMe() {
            try {
                t.interrupt();
            } catch (SecurityException e) {

            }
        }
    }
}
