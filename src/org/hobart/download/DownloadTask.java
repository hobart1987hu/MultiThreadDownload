package org.hobart.download;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 下载任务类
 * */
public class DownloadTask extends Thread{
    /**
     * 当前县城的id
     * */
    private int threadId;
    /**
     * 下载文件的URL
     * */
    private String url;
    /**
     * 下载保存到本地的文件
     * */
    private File file;
    /**
     * 当前线程所要下载文件的开始位置索引
     * */
    private int startIndex;
    /**
     * 当前线程所要下载文件的结束位置
     * */
    private int endIndex;

    public DownloadTask(int threadId,int startIndex,int endIndex,String url,File file){
        this.threadId = threadId;
        this.startIndex  = startIndex;
        this.endIndex  = endIndex;
        this.url =url;
        this.file = file;
    }
    @Override
    public void run() {
        try {
            System.out.println("run threadId:"+ threadId);
            HttpURLConnection connection =(HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10*1000);
            connection.setRequestProperty("Range","bytes="+startIndex+"-"+endIndex);
            final  int code = connection.getResponseCode();
            System.out.println("线程"+threadId+"： ResponseCode():"+code);
            if(code==200||code==206 ){
                System.out.println("线程"+threadId+"： 开始下载！");
                RandomAccessFile accessFile = new RandomAccessFile(file,"rw");
                accessFile.seek(startIndex);
                InputStream is = connection.getInputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len= is.read(buffer))!=-1){
                    accessFile.write(buffer,0,len);
                }
                accessFile.close();
                is.close();
                connection.disconnect();
                System.out.println("线程"+threadId+"： 下载完成！");
            }else {
                System.out.println("线程"+threadId+"： 下载未响应！");
            }
        }catch (Exception e){
            System.out.println("线程"+threadId+"： 下载失败！"+ e.getMessage());
        }
    }
}
