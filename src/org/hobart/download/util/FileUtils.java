package org.hobart.download.util;

public class FileUtils {

    public   static String getFileName(String path){
        return path.substring(path.lastIndexOf("/")+1);
    }
}
