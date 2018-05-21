package org.hobart.download.callback;

/**
 * 下载监听器
 */
public interface DownloadListener<T> {

    /**
     * 开始下载
     */
    void onStart(T t);

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

    public static class SimpleDownloadListener<T> implements DownloadListener<T> {

        @Override
        public void onStart(T ts) {

        }

        @Override
        public void onSuccess(T t) {

        }

        @Override
        public void onProgress(T t) {

        }

        @Override
        public void onFailure(T t, String message) {

        }
    }
}
