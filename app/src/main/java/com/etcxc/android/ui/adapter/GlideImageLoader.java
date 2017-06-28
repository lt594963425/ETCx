package com.etcxc.android.ui.adapter;
import android.content.Context;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.youth.banner.loader.ImageLoader;
/**
 * Created by 刘涛 on 2017/6/27 0027.
 */

public class GlideImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        Glide.with(context).load(path).into(imageView);
//            //Picasso 加载图片简单用法
//            Picasso.with(context).load("").into(imageView);
//            //用fresco加载图片简单用法，记得要写下面的createImageView方法
//            Uri uri = Uri.parse((String) path);
//            imageView.setImageURI(uri);
    }
   /*
        @Override
        public ImageView createImageView(Context context) {
            //使用fresco，需要创建它提供的ImageView
            SimpleDraweeView simpleDraweeView = new SimpleDraweeView(context);
            return simpleDraweeView;
        }*/
}
