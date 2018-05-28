# MultiThreadDownload
多线程下载文件
1、通过获取Accept-Ranges参数，判断需要被下载的文件是否支持range域下载，如果获取到的Accept-Ranges参数为不为空并且connection.getHeaderField("Accept-Ranges") == bytes 说明支持range域下载，否则不支持，
2、自定义多线程池，对支持range域下载下载的文件进行多线程分割下载
