package com.etcxc.android.ui.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.etcxc.android.R;
import com.youth.banner.loader.ImageLoader;

/**
 * 广告栏
 * Created by 刘涛 on 2017/6/27 0027.
 */

public class GlideImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        Glide.with(context).load(path).placeholder(R.mipmap.advinfo).error(R.mipmap.viewloading).into(imageView);
    }
}
