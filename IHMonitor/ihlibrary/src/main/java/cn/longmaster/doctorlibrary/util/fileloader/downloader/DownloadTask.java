package cn.longmaster.doctorlibrary.util.fileloader.downloader;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.longmaster.doctorlibrary.util.thread.UIThreadTask;


/**
 * 下载任务
 *
 * @author jinke 2016年11月21日
 */
class DownloadTask implements Runnable {
    public static final Object lock = new Object();
    private String filePath;
    private String url;
    private FileDownLoaderListener mFileDownLoaderListener;

    private boolean isCancle = false;
    private boolean isRunning = false;
    private Object cancleLock = new Object();

    public DownloadTask(String filePath, String url) {
        super();
        isCancle = false;
        this.filePath = filePath;
        this.url = url;
    }

    /**
     * 设置下载监听器
     *
     * @param fileDownloadListener
     */
    void setFileDownloadListener(FileDownLoaderListener fileDownloadListener) {
        this.mFileDownLoaderListener = fileDownloadListener;
    }

    @Override
    public void run() {
        synchronized (cancleLock) {
            if (isCancle) {
                return;
            } else {
                isRunning = true;
            }
        }
        File file = new File(filePath);
        int downloadResult;
        if (file.exists()) {
            downloadResult = FileDownLoaderListener.FILE_EXISTS;
        } else {
            if (url == null || url.trim().equals("")) {
                downloadResult = FileDownLoaderListener.URL_EMPTY;
            } else {
                OutputStream outputStream = null;
                HttpURLConnection httpURLConnection = null;
                try {
                    httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
                    httpURLConnection.setConnectTimeout(5000);
                    httpURLConnection.setReadTimeout(5000);
                    httpURLConnection.setRequestMethod("GET");
                    int responseCode = httpURLConnection.getResponseCode();
                    if (responseCode == 200) {
                        InputStream inputStream = httpURLConnection.getInputStream();

                        synchronized (lock) {
                            file.getParentFile().mkdirs();
                            file.createNewFile();
                        }
                        outputStream = new FileOutputStream(file);

                        byte[] buffer = new byte[1024];
                        int length = -1;
                        int totalSize = httpURLConnection.getContentLength();
                        int currentSize = 0;
                        long lastNotify = System.currentTimeMillis();

                        while ((length = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, length);
                            currentSize += length;
                            if ((System.currentTimeMillis() - lastNotify) > 200) {
                                new UiTaskProcess(totalSize, currentSize).execute();
                                lastNotify = System.currentTimeMillis();
                            }
                        }
                        outputStream.flush();
                        new UiTaskProcess(totalSize, currentSize).execute();
                        if (currentSize == totalSize) {
                            downloadResult = FileDownLoaderListener.SUCCESSFUL;
                        } else {
                            file.delete();
                            downloadResult = FileDownLoaderListener.FAILED;
                        }
                    } else {
                        file.deleteOnExit();
                        downloadResult = FileDownLoaderListener.FAILED;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    file.delete();
                    if (outputStream != null) {
                        try {
                            outputStream.flush();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    downloadResult = FileDownLoaderListener.FAILED;
                } finally {
                    closeQuietly(outputStream);
                    if (httpURLConnection != null)
                        httpURLConnection.disconnect();
                }
            }
            new UiTaskResult(downloadResult).execute();
        }
    }

    private static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    private class UiTaskProcess extends UIThreadTask {
        int totalSize;
        int currentSize;

        public UiTaskProcess(int totalSize, int currentSize) {
            super();
            this.totalSize = totalSize;
            this.currentSize = currentSize;
        }

        @Override
        protected void runOnUIThread() {
            if (mFileDownLoaderListener != null)
                mFileDownLoaderListener.onProgressChange(filePath, totalSize, currentSize);
        }
    }

    private class UiTaskResult extends UIThreadTask {
        int downloadResult;

        public UiTaskResult(int downloadResult) {
            super();
            this.downloadResult = downloadResult;
        }

        @Override
        protected void runOnUIThread() {
            if (mFileDownLoaderListener != null)
                mFileDownLoaderListener.onDownloadFinish(filePath, downloadResult);
        }
    }

    /**
     * 取消下载： 取消还未开始的下载，如果已经开始，则不能取消
     *
     * @return true：取消成功，将不会开始下载
     */
    public boolean cancel() {
        boolean result = false;
        synchronized (cancleLock) {
            if (!isCancle && !isRunning) {
                result = true;
                isCancle = true;
            } else {
                result = false;
            }
        }
        return result;
    }
}
