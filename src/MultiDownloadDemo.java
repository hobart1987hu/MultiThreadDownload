import org.hobart.download.MultiThreadDownloadTask;
import org.hobart.download.MultiThreadPool;
import org.hobart.download.callback.DownloadListener;
import org.hobart.download.util.LogUtils;

import java.util.logging.Logger;

public class MultiDownloadDemo {

    private static final Logger mLogger = LogUtils.getLogger("MultiDownloadDemo");
    static MultiThreadDownloadTask multiThreadDownloadTask = null;
    static int downloadCount = 5;

    public static void main(String[] args) {
        String url1 = "http://e.hiphotos.baidu.com/image/pic/item/8d5494eef01f3a2919fab6729525bc315c607c5f.jpg";
        String url2 = "https://github.com/facebook/react-native/archive/master.zip";
        String savePath = "/Users/huzeyin/Documents/testMultiDownload";

        try {
            multiThreadDownloadTask = new MultiThreadDownloadTask(1, url1, savePath, 7, new DownloadListener.SimpleDownloadListener<MultiThreadDownloadTask>() {
                @Override
                public void onStart(MultiThreadDownloadTask multiThreadDownload) {
//                    mLogger.info("TaskId:" + multiThreadDownload.getTaskId() + " 开始下载!");
                }

                @Override
                public void onSuccess(MultiThreadDownloadTask multiThreadDownload) {
                    System.out.println("multiThreadDownload.getTaskId()" + " 下载完成!");
                    downloadCount--;
                    if (downloadCount > 0) {
                        try {
                            Thread.sleep(2 * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println("重新下载文件：downloadCount: " + downloadCount);
                        multiThreadDownloadTask.startDownload();
                    } else {
                        MultiThreadPool.shutDownThreadPool();
                    }
//                    mLogger.info("TaskId:" + multiThreadDownload.getTaskId() + " 下载完成!");
                }

                @Override
                public void onProgress(MultiThreadDownloadTask multiThreadDownload) {
//                    mLogger.info("TaskId:" + multiThreadDownload.getTaskId() + " 下载进度：" + multiThreadDownload.getPercent());
                }

                @Override
                public void onFailure(MultiThreadDownloadTask multiThreadDownload, String message) {
//                    mLogger.info("TaskId:" + multiThreadDownload.getTaskId() + " 下载失败：" + message);
                }
            });
            multiThreadDownloadTask.startDownload();
        } catch (Exception e) {
            mLogger.info("下载异常:" + e.getMessage());
        }
    }
}
