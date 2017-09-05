package com.etcxc.android.net.callback;

import java.io.IOException;

import okhttp3.Response;

/**
 *  * Created by 刘涛
 */
public abstract class StringCallback extends Callback<String>
{
    @Override
    public String parseNetworkResponse(Response response, int id) throws IOException
    {
        return response.body().string();
    }
}
