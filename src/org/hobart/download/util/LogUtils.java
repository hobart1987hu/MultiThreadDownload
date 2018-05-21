package org.hobart.download.util;

import java.util.logging.Logger;

public class LogUtils {

    private static final String LOG_PREFIX="hobartMultiDownload_";

    public static Logger getLogger(String name ){
        return  Logger.getLogger(LOG_PREFIX+name);
    }

}
