package com.hanami.net.download;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author lidaisheng
 * @date 2019/3/20
 */
public class DownloadTask extends AsyncTask<String, Long, Long> {

    private static final String TAG = "DownloadTask";

    private DownloadListener mListener;
    private String mUrl;
    private String downloadFileName;
    private String downloadPath;

    public static final int CODE_URL_NULL = 1;
    public static final int CODE_APK_NOT_EXIST = 2;
    public static final int CODE_EXCEPTION = 3;

    private long total;

    public DownloadTask(String url, String fileName, String target, DownloadListener listener) {
        mUrl = url;
        downloadFileName = fileName;
        downloadPath = target;
        mListener = listener;
    }


    @Override
    protected Long doInBackground(String... strings) {
        if (mUrl == null) {
            mListener.onFailure(null, CODE_URL_NULL, "url 为 null");
            return null;
        }
        HttpURLConnection connection = null;
        InputStream is = null;
        OutputStream os = null;
        RandomAccessFile raf = null;
        try {
            File dir = new File(downloadPath);
            if (!dir.exists()) {
                dir.mkdir();
            }

            File file = new File(downloadPath, downloadFileName + ".apk");

            if (!file.exists()) {
                URL url = new URL(mUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("GET");
                if (!handleResponseCode(connection.getResponseCode())) {
                    return null;
                }
                os = new FileOutputStream(file);
                is = connection.getInputStream();
                file.createNewFile();
                byte[] buffer = new byte[1024];
                int inputSize;
                total = connection.getContentLength();
                long count = 0;
                while ((inputSize = is.read(buffer)) != -1) {
                    os.write(buffer, 0, inputSize);
                    count += inputSize;
                    publishProgress(count);
                    if (isCancelled()) {
                        os.flush();
                        return null;
                    }
                }
                os.flush();
            } else {
                URL url = new URL(mUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("GET");
                long readSize = file.length();
                connection.setRequestProperty("Range", "bytes=" + readSize + "-");
                if (!handleResponseCode(connection.getResponseCode())) {
                    return null;
                }
                is = connection.getInputStream();
                total = readSize + connection.getContentLength();
                raf = new RandomAccessFile(file, "rwd");
                raf.seek(readSize);
                byte[] buffer = new byte[1024];
                int inputSize;
                long count = readSize;
                if (count == total) {
                    onPostExecute(total);
                    return null;
                }
                while ((inputSize = is.read(buffer)) != -1) {
                    raf.write(buffer, 0, inputSize);
                    count += inputSize;
                    publishProgress(count);
                    if (isCancelled()) {
                        return null;
                    }
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            mListener.onFailure(e, CODE_EXCEPTION, "下载出错");
        } catch (IOException e) {
            e.printStackTrace();
            mListener.onFailure(e, CODE_EXCEPTION, "下载出错");
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
                if (raf != null) {
                    raf.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private boolean handleResponseCode(int code) {
        Log.i(TAG, "download response code is " + code);
        boolean result = true;
        switch (code) {
            case HttpURLConnection.HTTP_NOT_FOUND:
                mListener.onFailure(null, HttpURLConnection.HTTP_NOT_FOUND, "返回404");
                result = false;
                break;
            default:
                break;
        }
        return result;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Log.i(TAG, "task is canceled");
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
        Long current = values[0];
        Log.i(TAG, "task is progress update current is " + current + ", total is " + total);
        mListener.onUpdate(total, current);
    }

    @Override
    protected void onPostExecute(Long aLong) {
        super.onPostExecute(aLong);
        Log.i(TAG, "task is success");
        File file = new File(downloadPath, downloadFileName + ".apk");
        if (file.exists() && file.length() > 0) {
            mListener.onSuccess(file);
        } else {
            mListener.onFailure(null, CODE_APK_NOT_EXIST, "下载结束， apk文件没找到");
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.i(TAG, "task is start");
    }
}
