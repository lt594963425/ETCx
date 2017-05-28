package com.etcxc.android.base;

import java.io.IOException;


/**
 * 对具体的Protocol封装一个基类
 */

public abstract class BaseProtocol<ITEMBEAN> {

    private String mAppkey;
    private String mImei;
    private String mOs;
    private String mOsversion;
    private String mAppversion;
    private String mSourceid;
    private String mVer;

    /**
     * 加载数据
     *
     * @return
     * @throws IOException
     */
    public ITEMBEAN loadData(int index) throws IOException {

        return loadDataFromNet(index);
    }

    protected abstract ITEMBEAN loadDataFromNet(int index);
}


