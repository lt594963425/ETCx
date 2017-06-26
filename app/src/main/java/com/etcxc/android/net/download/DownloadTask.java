package com.etcxc.android.net.download;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.etcxc.android.utils.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

import static com.etcxc.android.net.OkClient.rightClient;
import static com.etcxc.android.net.download.DownloadConfig1.REASON_NET_FAILED;

/**
 * 建议下载的OkHttpClien不与普通请求共用，okHttp默认对相同host的最大并发数是５,以免阻塞其他网络请求，
 * Created by xwpeng on 17-2-14.
 */

public class DownloadTask {
    public static final String TAG = "DownloadTask";
    private static final int BUFFER_SIZE = 2048;
    private Call mCall;
    private DownloadOptions mOptions;
    private OnDownloadLister mOndownloadLister;
    //是否进行了cookies重试
    private boolean isRetried;

    public DownloadTask(DownloadOptions options, OnDownloadLister lister) {
        if (options == null || lister == null) return;
        mOptions = options;
        mOndownloadLister = lister;
    }

    public String getUrl() {
        return mOptions.url;
    }

    public void cancle() {
        if (mCall != null && mCall.isExecuted()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mCall.cancel();
                }
            }).start();
        }
    }

    /**
     * 是否断点下载
     */
    private boolean isBreakpoint(DownloadOptions options) {
        long start = options.start;
        long end = options.end;
        return start > 0 && end > 0 && end > start;
    }

    private Call assemblyCall() {
        Request.Builder builder = new Request.Builder().url(mOptions.url);
        builder.addHeader("Cache-Control", "max-age=0");
        builder.addHeader("Accept-Encoding", "q=1.0, identity");
        builder.addHeader("X-CM-SERVICE", "L-Android");
        if (!TextUtils.isEmpty(mOptions.cookie)) builder.addHeader("Cookie", mOptions.cookie);
        if (isBreakpoint(mOptions))
            builder.addHeader("Range", "bytes=" + mOptions.start + "-" + mOptions.end);
        return rightClient(mOptions.url)
                .newBuilder()
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(1, TimeUnit.MINUTES)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build().newCall(builder.build());
    }

    /**
     * 开始下载
     */
    public void start() {
        mCall = assemblyCall();
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                mOndownloadLister.onFailed(mOptions, REASON_NET_FAILED);
                LogUtil.e(TAG, e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (isBreakpoint(mOptions)) {
                    //downloading2(options, response, lister);}
                } else downloading(response);

            }
        });
    }

    /**
     * todo
     * 判断response内容是否cookies不可用，是重设cookies返回true.其他都返回false
     */

    private boolean invalidCookiesRetry(DownloadOptions options, InputStream is) {
//        if (is == null || isRetried) return false;
//        String string = StringUtil.inputStreamToString(is);
//        if (!TextUtils.isEmpty(string) && string.contains("FA_INVALID_SESSION")) {
//            isRetried = true;
//            start();
//            return true;
//        }
        return false;
    }

    private File getTargetFile(String path) throws IOException {
        File file = new File(path + ".temp");
        if (!file.exists()) {
            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
            file.createNewFile();
        }
        return file;
    }

    /**
     * 正常下载
     */
    private void downloading(Response response) {
        if (HttpURLConnection.HTTP_OK == response.code()) {
            long total = response.body().contentLength();
            //   if (total < 1)//                total = mOptions.; 服务器文件长度
            mOndownloadLister.onStart(mOptions, total);
            InputStream is = null;
            FileOutputStream fos = null;
            byte[] buf = new byte[BUFFER_SIZE];
            int len;
            long timeFlag = 0;
            try {
                is = response.body().byteStream();
                File file = getTargetFile(mOptions.targetPath);
                fos = new FileOutputStream(file);
                long finished = 0;
                while ((len = is.read(buf)) != -1) {
                    finished += len;
                    fos.write(buf, 0, len);
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    long t = System.currentTimeMillis();
                    if (t - timeFlag > 100) {
                        timeFlag = t;
                        LogUtil.e(TAG, "" + finished);
                        mOndownloadLister.onProgress(mOptions, finished, total);
                    }
                }
                fos.flush();
                file.renameTo(new File(mOptions.targetPath));
                //完成量更新给Options,将会作为文件大小写到数据库，用来检测手机目录文件是否存在．
                mOptions.finished = finished;
                mOndownloadLister.onSucceed(mOptions);
            } catch (IOException e) {
                mOndownloadLister.onFailed(mOptions, mCall.isCanceled() ? DownloadConfig1.REASON_CANCELED : REASON_NET_FAILED);
            } finally {
                try {
                    if (is != null) is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (fos != null) fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                response.body().close();
            }
        } else if (!invalidCookiesRetry(mOptions, response.body().byteStream())) {
            mOndownloadLister.onFailed(mOptions, REASON_NET_FAILED);
        }
    }

    public interface OnDownloadLister {
        void onQueue(DownloadOptions options);

        void onStart(DownloadOptions options, long total);

        void onProgress(DownloadOptions options, long finished, long total);

        void onFailed(DownloadOptions options, int reason);

        void onSucceed(DownloadOptions options);
    }
}
