package com.etcxc.android.net.download;

import android.content.Intent;

import com.etcxc.android.base.App;
import com.etcxc.android.net.Actions;

/**
 * 下载管理类
 * Created by xwpeng on 2017/6/15.
 */

public class DownloadManger {

    public static void download(DownloadOptions options) {
        Intent intent = new Intent();
        intent.setAction(Actions.ACTION_DSERVICE);
        intent.setPackage(App.get().getPackageName());
        intent.putExtra("options", options);
        App.get().startService(intent);
    }

    public static void cancle(String url) {
        Intent intent = new Intent();
        intent.setAction(Actions.ACTION_DSERVICE);
        intent.setPackage(App.get().getPackageName());
        intent.putExtra("url", url);
        intent.putExtra("cancel", true);
        App.get().startService(intent);
    }
}
