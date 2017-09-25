package com.etcxc.android.net.callback;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import okhttp3.Response;

/**
 * * Created by LiuTao
 */

public abstract class BitmapCallback extends Callback<Bitmap> {
    @Override
    public Bitmap parseNetworkResponse(Response response, int id) throws Exception {
        //if(response.header("Content-Type").equals("text/html; charset=utf-8"))
        return BitmapFactory.decodeStream(response.body().byteStream());
    }

}
