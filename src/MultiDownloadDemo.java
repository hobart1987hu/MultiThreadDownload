import org.hobart.download.MultiThreadDownloadTask;
import org.hobart.download.callback.DownloadListener;
import org.hobart.download.util.LogUtils;

import java.util.logging.Logger;

public class MultiDownloadDemo {

    private static final Logger mLogger = LogUtils.getLogger("MultiDownloadDemo");

    public static void main(String[] args) {
        //https://github.com/hobart1987hu/SimpleHybirdNative/archive/master.zip
        //https://github.com/facebook/react-native/archive/master.zip
        String url1 = "https://github.com/hobart1987hu/SimpleHybirdNative/archive/master.zip";
        String url2 = "https://github.com/facebook/react-native/archive/master.zip";
        String savePath = "/Users/huzeyin/Documents/testMultiDownload";
        try {
            MultiThreadDownloadTask multiThreadDownloadTask = new MultiThreadDownloadTask(1, url2, savePath, 4, new DownloadListener.SimpleDownloadListener<MultiThreadDownloadTask>() {

                @Override
                public void onStart(MultiThreadDownloadTask multiThreadDownload) {
                    mLogger.info("TaskId:" + multiThreadDownload.getTaskId() + " 开始下载!");
                }

                @Override
                public void onSuccess(MultiThreadDownloadTask multiThreadDownload) {
                     mLogger.info("TaskId:" + multiThreadDownload.getTaskId() + " 下载完成!");
                }

                @Override
                public void onProgress(MultiThreadDownloadTask multiThreadDownload) {
                     mLogger.info("TaskId:" + multiThreadDownload.getTaskId() + " 下载进度：" + multiThreadDownload.getPercent());
                }

                @Override
                public void onFailure(MultiThreadDownloadTask multiThreadDownload, String message) {
                      mLogger.info("TaskId:" + multiThreadDownload.getTaskId() + " 下载失败：" + message);
                }
            });
            multiThreadDownloadTask.startDownload();
        } catch (Exception e) {
            mLogger.info("下载异常:" + e.getMessage());
        }
    }
}
