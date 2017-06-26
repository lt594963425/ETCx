package com.etcxc.android.net.upload;

import com.etcxc.android.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

import static com.etcxc.android.net.download.DownloadTask.TAG;

/**
 * 上传任务
 * Created by xwpeng on 2017/6/24.
 */

public class UploadTask {

    private Call mCall;


    public static Call getUploadCall(String url, final File file, String cookie, RequestBody fileBody) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", file.getName(), fileBody)
                .build();
        Request request = new Request.Builder()
                .url(url)
//                .addHeader("Cookie", cookie)
                .post(requestBody)
                .build();
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS);
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(logging).build();
        return builder.build().newCall(request);
    }

    public static Call getUploadCall(String url, String cookie , List<File> files) {
        MultipartBody.Builder partBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        for (File file : files) {
            partBuilder.addFormDataPart("image[]", file.getName(), RequestBody.create(MediaType.parse("image"), file));
        }
        Request request = new Request.Builder()
                .url(url)
//                .addHeader("Cookie", cookie)
                .post(partBuilder.build())
                .build();
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS);
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(logging).build();
        return builder.build().newCall(request);
    }

    /**
     *同步执行上传
     * RequestBody.create(MediaType.parse("application/octet-stream"), file);
     * createProgressRequestBody(MediaType.parse("application/octet-stream"), file)
     */
    public String doUpload(String url, final File file, String cookie) {
        mCall = getUploadCall(url, file, cookie, RequestBody.create(MediaType.parse("image"), file));
        String result = null;
        try {
            result = mCall.execute().body().string();
        } catch (IOException e) {
            LogUtil.e(TAG, "doUpload", e);
        }
        return result;
    }

    /**
     *可监听上传进度的RequestBody
     */
    public <T> RequestBody createProgressRequestBody(final MediaType contentType, final File file) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return contentType;
            }

            @Override
            public long contentLength() {
                return file.length();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                Source source;
                try {
                    source = Okio.source(file);
                    Buffer buf = new Buffer();
                    long remaining = contentLength();
                    long finish = 0;
                    for (long readCount; (readCount = source.read(buf, 2048)) != -1; ) {
                        sink.write(buf, readCount);
                        finish += readCount;
//                        mUploadOption.finished = finish;
//                        mUploadOption.total = remaining;
//                        mUploadCallback.onUploadProgress(mUploadOption);
                    }
                } catch (Exception e) {
                    LogUtil.e(TAG, e.getMessage());
                }
            }
        };
    }
}
