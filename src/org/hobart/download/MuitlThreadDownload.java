package org.hobart.download;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 * 多线程下载
 * */
public class MuitlThreadDownload {

    public void multiDownload(String downloadUrl,String fileSavePath,int threadSize) throws Exception{
        if(threadSize<1){
            throw new Exception("至少有一个线程！");
        }
        URL url = new URL(downloadUrl);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5*1000);
        connection.setReadTimeout(5*1000);
        if(connection.getResponseCode() ==200){
            final  int length = connection.getContentLength();
            File file =new File(fileSavePath,getFileName(downloadUrl));
            RandomAccessFile accessFile = new RandomAccessFile(file,"rw");
            accessFile.setLength(length);
            accessFile.close();
            int blockSize= length/threadSize;
            for(int i=0;i<threadSize;i++){
                int startIndex=  i*blockSize;
                int endIndex= (i+1)*blockSize -1;
                if(i==threadSize-1){
                    blockSize = length;
                }
                System.out.println("线程"+i+" startIndex:"+startIndex+ " endIndex:"+endIndex);
                new DownloadTask(i,startIndex,endIndex,downloadUrl,file).start();
            }
        }else{
            throw  new Exception("下载未响应");
        }
    }
    private  static String getFileName(String path){
        return path.substring(path.lastIndexOf("/")+1);
    }
}
