package org.hobart.download.callback;

/**
 * 下载监听器
 */
public interface DownloadListener<T> {

    /**
     * 下载成功回调
     */
    void onSuccess(T t);

    /**
     * 下载进度回掉
     */
    void onProgress(T t);

    /**
     * 下载失败回掉
     *
     * @param message
     */
    void onFailure(T t, String message);


}
