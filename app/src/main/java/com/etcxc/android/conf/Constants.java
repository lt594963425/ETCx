package com.etcxc.android.conf;

import com.etcxc.android.utils.LogUtils;

/**
 * 常量类
 */
public class Constants {
    /**
     * LogUtils.LEVEL_ALL:打开日志(显示所有的日志输出)
     * LogUtils.LEVEL_OFF:关闭日志(屏蔽所有的日志输出)
     */
    public static final int DEBUGLEVEL = LogUtils.LEVEL_ALL;
    public static final long PROTOCOL_TIMEOUT = 5 * 60 * 1000;
    public static final class Url {
        public static final String BASE_URL = "";

        public static final String IMAGE_URL = "";
    }
}
