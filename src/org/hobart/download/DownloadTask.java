package org.hobart.download;


import org.hobart.download.callback.DownloadListener;
import org.hobart.download.util.DownLoadState;
import org.hobart.download.util.LogUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

/**
 * 下载任务类
 */
public class DownloadTask implements Runnable {

    private static final Logger mLogger = LogUtils.getLogger(DownloadTask.class.getName());
    private final int BUFF_LEN = 1024 * 1024;

    private DownLoadState state;

    /**
     * 当前县城的id
     */
    private int threadId;
    /**
     * 下载文件的URL
     */
    private String url;
    /**
     * 下载保存到本地的文件
     */
    private File file;
    /**
     * 当前线程所要下载文件的开始位置索引
     */
    private int startIndex;
    /**
     * 当前线程所要下载文件的结束位置
     */
    private int endIndex;
    /**
     * 需要下载的总的数据量
     */
    private int totalSize;
    /**
     * 已经下载的数据量
     */
    private int downloadSize;

    /**
     * 线程内部下载监听器
     */
    private DownloadListener<DownloadTask> innerDownloadListener;

    public DownloadTask(int threadId, int startIndex, int endIndex, String url, File file, DownloadListener<DownloadTask> listener) {
        this.threadId = threadId;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.totalSize = endIndex - startIndex;
        this.url = url;
        this.file = file;
        this.innerDownloadListener = listener;
        this.state = DownLoadState.WAIT;
    }

    @Override
    public void run() {
        try {

            if (null != innerDownloadListener) innerDownloadListener.onStart(this);

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10 * 1000);
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type", "application/zip");
            connection.setRequestProperty("Accept-Encoding", "");
            connection.setRequestProperty("Range", "bytes=" + startIndex + "-" + endIndex);
            final int code = connection.getResponseCode();
            mLogger.info("线程" + threadId + "： ResponseCode():" + code);
            if (code == 200 || code == 206) {
                mLogger.info("线程" + threadId + "： 开始下载！");
                RandomAccessFile accessFile = new RandomAccessFile(file, "rwd");
                accessFile.seek(startIndex);
                InputStream is = connection.getInputStream();
                //System.out.println("线程" + threadId + "： content length：" + connection.getContentLength());
                //long skipStartTime = System.currentTimeMillis();
                //is.skip(startIndex);
                //System.out.println("线程" + threadId + "： skip 花费时间：" + (System.currentTimeMillis() - skipStartTime) / 1000 + "秒");
                BufferedInputStream buffInput = new BufferedInputStream(is, BUFF_LEN);
                byte[] buffer = new byte[BUFF_LEN];
                int len;
                int current = 0;
                int dataLen = 0;
                while ((len = buffInput.read(buffer)) != -1) {
                    if (totalSize - downloadSize < len) {
                        dataLen = totalSize - downloadSize;
                    } else {
                        dataLen = len;
                    }
                    accessFile.write(buffer, 0, dataLen);
                    current += dataLen;
                    downloadSize = current;
                    this.state = DownLoadState.DOWNLOADING;
                    mLogger.info("线程" + threadId + " 下载进度： " + downloadSize + " totalSize:" + totalSize);
                    if (null != innerDownloadListener)
                        innerDownloadListener.onProgress(this);
                    if (downloadSize >= totalSize) {
                        break;
                    }
                }
                this.state = DownLoadState.FINISHED;
                if (null != innerDownloadListener)
                    innerDownloadListener.onSuccess(this);
                mLogger.info("线程" + threadId + "： 下载完成！");
                accessFile.close();
                is.close();
                buffInput.close();
                connection.disconnect();
            } else {
                this.state = DownLoadState.FAILURE;
                if (null != innerDownloadListener) {
                    innerDownloadListener.onFailure(this, "下载未响应");
                }
                mLogger.info("线程" + threadId + "： 下载未响应！");
            }
        } catch (Exception e) {
            this.state = DownLoadState.FAILURE;
            if (null != innerDownloadListener) {
                innerDownloadListener.onFailure(this, e.getMessage());
            }
            mLogger.info("线程" + threadId + "： 下载失败！" + e.getMessage());
        }
    }

    public int getDownloadSize() {
        return this.downloadSize;
    }

    public DownLoadState getDownLoadState() {
        return state;
    }
}