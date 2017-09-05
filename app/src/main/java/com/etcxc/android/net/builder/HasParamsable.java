package com.etcxc.android.net.builder;

import java.util.Map;

/**
 * * Created by 刘涛
 */
public interface HasParamsable
{
    OkHttpRequestBuilder params(Map<String, String> params);
    OkHttpRequestBuilder addParams(String key, String val);
}
