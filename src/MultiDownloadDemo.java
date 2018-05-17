import org.hobart.download.MuitlThreadDownload;

public class MultiDownloadDemo {

    public static void main(String [] args){
        String url = "https://github.com/facebook/react-native/archive/master.zip";
        String savePath = "/Users/huzeyin/Documents/testMultiDownload";
        try {
            new MuitlThreadDownload().multiDownload(url,savePath,5);
        }catch (Exception e){
            System.out.println("下载异常:"+e.getMessage());
        }
    }
}
