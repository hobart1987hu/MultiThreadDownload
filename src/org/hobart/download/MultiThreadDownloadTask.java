package org.hobart.download;

import org.hobart.download.callback.DownloadListener;
import org.hobart.download.util.DownLoadState;
import org.hobart.download.util.FileUtils;
import org.hobart.download.util.LogUtils;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

/**
 * 多线程下载
 */
public class MultiThreadDownloadTask {

    private static final Logger mLogger = LogUtils.getLogger(MultiThreadDownloadTask.class.getName());
    /**
     * 下载的URL
     */
    private String url;
    /**
     * 下载后，文件保存的路径
     */
    private String fileSavePath;
    /**
     * 下载当前文件，设置的线程数
     */
    private int threadCount;
    /**
     * 当前下载文件的总的大小
     */
    private int totalSize;
    /**
     * 下载进度
     */
    private double percent;
    /**
     * 多线程下载所有的tasks
     */
    private List<DownloadTask> allTask;
    /**
     * 下载失败的tasks
     */
    private List<DownloadTask> failedTasks;
    /**
     * 下载进度回调
     */
    private DownloadListener<MultiThreadDownloadTask> outListener;

    /**
     * 下载进度定时器
     */
    private Timer progressTimer;

    /**
     * 当前下载的taskId
     */
    private int taskId;

    private long startDownLoadTime;


    public MultiThreadDownloadTask(int taskId, String url, String fileSavePath, int threadCount, DownloadListener<MultiThreadDownloadTask> listener) {
        this.taskId = taskId;
        if (threadCount < 1)
            threadCount = 1;
        this.url = url;
        this.fileSavePath = fileSavePath;
        this.threadCount = threadCount;
        this.allTask = Collections.synchronizedList(new ArrayList<>(threadCount));
        this.failedTasks = Collections.synchronizedList(new ArrayList<>());
        this.outListener = listener;
        this.progressTimer = new Timer();
    }

    public void startDownload() {
        try {

            startDownLoadTime = System.currentTimeMillis();

            URL url = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5 * 1000);
            connection.setReadTimeout(5 * 1000);
            if (connection.getResponseCode() == 200) {
                final int length = connection.getContentLength();
                String supportRanges = connection.getHeaderField("Accept-Ranges");
                mLogger.info("taskId：" + taskId + " supportRanges:" + supportRanges);
                boolean enableRanges = false;
                if (null != supportRanges && supportRanges.equals("bytes")) {
                    enableRanges = true;
                } else {
                    enableRanges = false;
                }
                if (!enableRanges) {
                    threadCount = 1;
                }
                connection.disconnect();
                totalSize = length;
                mLogger.info("taskId：" + taskId + " 文件总大小为：" + totalSize);
                File file = new File(fileSavePath, FileUtils.getFileName(this.url));
                RandomAccessFile accessFile = new RandomAccessFile(file, "rwd");
                accessFile.setLength(length);
                accessFile.close();
                int blockSize = length / threadCount;

                if (null != outListener)
                    outListener.onStart(this);

                for (int i = 0; i < threadCount; i++) {
                    int startIndex = i * blockSize;
                    int endIndex = (i + 1) * blockSize - 1;
                    if (i == threadCount - 1) {
                        endIndex = length;
                    }
                    mLogger.info("线程：" + i + " startIndex:" + startIndex + "  endIndex:" + endIndex);
                    DownloadTask downloadTask = new DownloadTask(i, startIndex, endIndex, this.url, file, new DownloadListener.SimpleDownloadListener<DownloadTask>() {
                        @Override
                        public void onFailure(DownloadTask task, String message) {
                            failedTasks.add(task);
                            //如果都下载失败了,去掉定时器
                            if (failedTasks.size() >= threadCount) {
                                progressTimer.cancel();
                                if (null != outListener) outListener.onFailure(MultiThreadDownloadTask.this, "下载失败！");
                            }
                        }
                    });
                    allTask.add(downloadTask);
                    MultiThreadPool.execute(downloadTask);
                }
                scheduleProgressTask();
            } else {
                printDownloadCostTime();
                mLogger.info("下载 url:" + this.url + "\n未响应");
                if (null != outListener) outListener.onFailure(this, "未响应");
            }
        } catch (Exception e) {
            printDownloadCostTime();
            mLogger.info("下载 url:" + this.url + "异常 \n错误信息：" + e.getMessage());
            if (null != outListener) outListener.onFailure(this, e.getMessage());
        }
    }

    private void scheduleProgressTask() {
        progressTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                int downloadSize = 0;
                boolean isAllFinished = true;
                for (int i = 0; i < threadCount; i++) {
                    DownloadTask task = allTask.get(i);
                    downloadSize += task.getDownloadSize();
                    if (task.getDownLoadState() != DownLoadState.FINISHED) {
                        isAllFinished = false;
                    }
                }
                percent = (float) downloadSize / (float) totalSize;
                if (isAllFinished) {
                    percent = 1.0;
                    printDownloadCostTime();
                }
                if (null != outListener) {
                    outListener.onProgress(MultiThreadDownloadTask.this);
                }
                if (isAllFinished) {
                    cancel();
                }
            }
        }, 1 * 1000, 1 * 1000);
    }

    private void printDownloadCostTime() {
        long duration = System.currentTimeMillis() - startDownLoadTime;
        mLogger.info("taskId:" + taskId + " 下载耗时：" + duration / 1000 + "秒");
    }

    public void clear() {
        this.allTask.clear();
        this.failedTasks.clear();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }
}
