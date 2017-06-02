package com.etcxc.android.factory;
/**
 * Created by 刘涛 on 2017/5/27 0027.
 */

public class ThreadPoolProxyFactory {
    private static ThreadPoolProxy mNormalThreadPoolProxy;
    private static ThreadPoolProxy mDownloadThreadPoolProxy;

    /**
     * 得到普通线程池
     * @return
     */
    public static ThreadPoolProxy getNormalThreadPoolProxy() {
        if (mNormalThreadPoolProxy ==null) {
            synchronized (ThreadPoolProxyFactory.class) {
                if (mNormalThreadPoolProxy == null) {
                    mNormalThreadPoolProxy = new ThreadPoolProxy(5, 5);
                }
            }
        }
        return mNormalThreadPoolProxy;
    }

    /**
     * 得到下载线程池
     * @return
     */
    public static ThreadPoolProxy getDownloadThreadPoolProxy() {
        if (mDownloadThreadPoolProxy ==null) {
            synchronized (ThreadPoolProxyFactory.class) {
                if (mDownloadThreadPoolProxy == null) {
                    mDownloadThreadPoolProxy = new ThreadPoolProxy(5, 5);
                }
            }
        }
        return mDownloadThreadPoolProxy;
    }
}
