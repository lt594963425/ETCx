package com.etcxc.android.net.download;

/**
 * 下载常量配置
 * Created by xwpeng on 2017/6/15.
 */
public class DownloadConfig1 {
    public static final int STOP_ = 1;
    public final static int REASON_NET_FAILED = -1;
    public final static int REASON_CANCELED = -2;
    //下载过程处于当一步
    //每一步都带tag和stag
    public final static int STEP_QUEUE = 1;
    public final static int STEP_START = 2;//带total
    public final static int STEP_PROGRESS = 3;//带finished, total
    public final static int STEP_SUCCEED = 4;
    public final static int STEP_FAILED = 5;//带reason
    public final static int STEP_CANCELED = 6;

    public final static String KEY_DOWNLOAD = "download";
}
