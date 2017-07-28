package com.etcxc.android.bean;

/**
 * 接口调用记录
 * Created by xwpeng on 2017/7/28.
 */

public class ApiRecord {
    public String name;
    public String url;
    public String requestData;
    public long requestTime;
    public String responseData;
    public long responseTime;
    public String uid;

    @Override
    public String toString() {
        return "ApiRecord{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", requestData='" + requestData + '\'' +
                ", requestTime=" + requestTime +
                ", responseData='" + responseData + '\'' +
                ", responseTime=" + responseTime +
                ", uid='" + uid + '\'' +
                '}';
    }
}
