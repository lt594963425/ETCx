package com.etcxc.android.net.download;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.etcxc.android.R;

import java.util.HashMap;
import java.util.Map;

/**
 * 下载过程中，在通知栏显示下载进度
 * Created by xwpeng on 2017/6/15.<br/>
 */
public class DownloadNotification {
    NotificationManagerCompat mManager;
    private Map<String, NotificationCompat.Builder> mBuilders;
    private Context mContext;

    public DownloadNotification(Context context) {
        this.mContext = context;
    }

    public void notifyProgress(DownloadOptions o) {
        NotificationCompat.Builder builder = builder(o);
        int progress = o.progress();
        Notification notification = new NotificationCompat.BigTextStyle(builder)
                .bigText(progress == 100 ? mContext.getString(R.string.download_finish) : (progress + "%"))
                .build();
        if (mManager == null) mManager = NotificationManagerCompat.from(mContext);
        mManager.notify(1, notification);
    }

    private NotificationCompat.Builder builder(DownloadOptions o) {
        if (mBuilders == null) mBuilders = new HashMap<>();
        Object object = mBuilders.get(o.url);
        NotificationCompat.Builder builder;
        if (object != null) {
            builder = (NotificationCompat.Builder) object;
        } else {
            builder = new NotificationCompat.Builder(mContext)
                    .setAutoCancel(true)
                    .setContentInfo("")
                    .setContentTitle("111")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(android.R.drawable.stat_sys_download)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);
            mBuilders.put(o.url, builder);
        }
        builder.setContentIntent(pi(o));
        int progress = o.progress();
        builder.setProgress(100, progress, false);
        if (progress == 100) {
            builder.setSmallIcon(android.R.drawable.stat_sys_download_done);
        }
        return builder;
    }

    private PendingIntent pi(DownloadOptions o) {
     /*   if (o.step == DownloadConfig1.STEP_SUCCEED) {
            Intent intent = new Intent(Actions.ACTION_STATUSBAR_MESSAGE1);
            intent.putExtra("download", download);
            return PendingIntent.getActivity(mContext, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            return null;
        }*/
        return null;
    }

}
