package org.hobart.download.util;

public enum DownLoadState {
    /**
     * 等待状态
     */
    WAIT,
    /**
     * 正在下载
     */
    DOWNLOADING,
    /**
     * 下载完成
     */
    FINISHED,
    /**
     * 下载失败
     */
    FAILURE;
}
