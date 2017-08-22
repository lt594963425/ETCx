package com.etcxc.android.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.util.LruCache;
import android.support.v7.widget.AppCompatDrawableManager;
import android.util.Log;

import com.etcxc.MeManager;
import com.etcxc.android.R;
import com.etcxc.android.net.NetConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.etcxc.android.net.FUNC.GET_HEAD;
import static com.etcxc.android.utils.FileUtils.getCachePath;

/**
 * Created by LiuTao on 2017/8/21 0021.
 */

public class LoadImageHeapler {
    protected final String TAG = "LoadImageHeapler";
    //缓存类，能过获取和写入数据到缓存中，短时间的存储！！
    private static LruCache<String, Bitmap> cache;
    //文件操作类对象
    private Activity context;
    private Bitmap mBitmap;

    /**
     * 构造方法，需要传入一个保存文件的名字
     * 实例化：线程池对象，缓存类，文件操作类对象
     */

    public LoadImageHeapler(Activity context, String dirName) {
        this.context = context;
        //获取系统分配的最大内存
        int maxSize = (int) (Runtime.getRuntime().maxMemory() / 8);
        //实例化缓存类的对象
        cache = new LruCache<String, Bitmap>(maxSize) {
            //每一个键所对应的值的大小
            //自动释放低频率的文件
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };

    }

    public void loadUserHead(ImageLoadListener listener) {
        Log.e(TAG, "从网络中下载数据");
        requestHead(listener);

    }

    private void requestHead(ImageLoadListener listener) {
        OkHttpClient client = new OkHttpClient();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", MeManager.getUid());
            jsonObject.put("token", MeManager.getToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(TAG, jsonObject.toString());
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), String.valueOf(jsonObject));
        Request request = new Request.Builder()
                .url(NetConfig.HOST + GET_HEAD)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast(R.string.request_failed);
                        //Bitmap bmp=BitmapFactory.decodeResource(getResources(), R.drawable.vd_head2);
                        Bitmap bmp = getBitmapFromVectorDrawable(context, R.drawable.vd_head2);
                        listener.loadImage(bmp);
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.header("Content-Type").equals("text/html; charset=utf-8")){
                    InputStream inputStream = response.body().byteStream();
                    mBitmap = BitmapFactory.decodeStream(inputStream);
                    if (mBitmap != null) {
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.loadImage(mBitmap);
                            }
                        });
                    }
                }else {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showToast(R.string.request_failed);
                            //Bitmap bmp=BitmapFactory.decodeResource(getResources(), R.drawable.vd_head2);
                            Bitmap bmp = getBitmapFromVectorDrawable(context, R.drawable.vd_head2);
                            listener.loadImage(bmp);

                        }
                    });
                }
            }
        });
    }
    /**
     * 定义一个接口，里面有一个方法，
     * 这里有一个Bitmap对象参数，作用是让调用这接收这个Bitmap对象，实际这bitmap对象就是缓存中的对象
     */
    public interface ImageLoadListener {
        void loadImage(Bitmap bmp);
    }

    /**
     * 使用缓存类获取Bitmap对象
     */
    private Bitmap readFromCache(String key) {
        return cache.get(key);
    }

    /**
     * 文件的读取，
     * 根据文件的名字，读取出一个Bitmap的对象，
     * 如果之前保存过就有值，否则是null
     */
    //  mFile = new File(getCachePath(getActivity()), "user-avatar.jpg");
    public Bitmap readFromSDCard(String key) {
        return BitmapFactory.decodeFile(new File(getCachePath(context), key).getAbsolutePath());
    }

    /**
     * drawable 转bitmap
     *
     * @param context
     * @param drawableId
     * @return
     */
    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = AppCompatDrawableManager.get().getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
