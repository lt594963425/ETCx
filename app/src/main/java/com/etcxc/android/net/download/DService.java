package com.etcxc.android.net.download;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Created by xwpeng on 2017/6/15.<br/>
 * 处理下载的Service,可并发．断点下载不可用，待实现．
 */
public class DService extends Service {
    private static final String TAG = "DService";
    private volatile List<DownloadTask> mTasks = Collections.synchronizedList(new CopyOnWriteArrayList<DownloadTask>());

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getBooleanExtra("cancel", false)) {
            String url = intent.getStringExtra("url");
            if (TextUtils.isEmpty(url)) return super.onStartCommand(intent, flags, startId);
                for (DownloadTask task : mTasks) {
                    if (url.equals(task.getUrl())) task.cancle();
                }
        } else {
            DownloadOptions options = intent.getParcelableExtra("options");
            if (!checkDownloadOptions(options)) return super.onStartCommand(intent, flags, startId);
            int index = getTaskIndex(options.url);
            if (index < 0) {
                DownloadTask task = new DownloadTask(options, mOnDownloadLister);
                mTasks.add(task);
                task.start();
                mOnDownloadLister.onQueue(options);
            }
            if (mTasks.isEmpty()) {
                stopSelf();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private DownloadTask.OnDownloadLister mOnDownloadLister = new DownloadTask.OnDownloadLister() {
        @Override
        public void onQueue(DownloadOptions o) {
            o.step = DownloadConfig1.STEP_QUEUE;
            broadcast(o);

        }

        @Override
        public void onStart(DownloadOptions o, long total) {
            o.total = total;
            o.step = DownloadConfig1.STEP_START;
            broadcast(o);
        }

        @Override
        public void onProgress(DownloadOptions o, long finished, long total) {
            o.finished = finished;
            if (o.total == 0) o.total = total;
            if (o.step != DownloadConfig1.STEP_PROGRESS) o.step = DownloadConfig1.STEP_PROGRESS;
            broadcast(o);
        }

        @Override
        public void onFailed(DownloadOptions o, int reason) {
            o.step = DownloadConfig1.STEP_FAILED;
            o.reason = reason;
            broadcast(o);
            removeTask(o.url);
            if (mTasks.isEmpty()) stopSelf();
        }

        @Override
        public void onSucceed(DownloadOptions o) {
            o.step = DownloadConfig1.STEP_SUCCEED;
            broadcast(o);
            removeTask(o.url);
            if (mTasks.isEmpty()) {
                stopSelf();
            }
        }
    };

    private void removeTask(String url) {
        int target = -1;
        int index = 0;
        for (DownloadTask task : mTasks) {
            if (url.equals(task.getUrl())) {
                target = index;
                break;
            }
            index++;
        }
        if (target > -1) {
            mTasks.remove(target);
        }
    }

    private int getTaskIndex(String url) {
        int i = 0;
        for (DownloadTask o : mTasks) {
            if (url.equals(o.getUrl())) return i;
            i++;
        }
        return -1;
    }

    /**
     * 检查{@link DownloadOptions}是否合法，不合法则不下载
     */
    private boolean checkDownloadOptions(DownloadOptions options) {
        boolean pass = options != null && !TextUtils.isEmpty(options.url) && !TextUtils.isEmpty(options.targetPath);
        return pass;
    }

    private DownloadNotification dn;

    private void broadcast(DownloadOptions options) {
//        showNotificationIfNeeded(options);
        EventBus.getDefault().post(options);
    }

    private void showNotificationIfNeeded(DownloadOptions o) {
        if (o.showNotification) {
            if (dn == null) {
                dn = new DownloadNotification(getApplicationContext());
            }
            dn.notifyProgress(o);
        }
    }
}
