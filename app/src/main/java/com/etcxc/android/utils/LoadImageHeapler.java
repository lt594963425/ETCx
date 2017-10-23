package com.etcxc.android.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.etcxc.MeManager;
import com.etcxc.android.R;
import com.etcxc.android.base.App;
import com.etcxc.android.net.NetConfig;
import com.etcxc.android.net.OkHttpUtils;
import com.etcxc.android.net.callback.BitmapCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import okhttp3.Call;

import static com.etcxc.android.net.FUNC.GET_HEAD;
import static com.etcxc.android.utils.FileUtils.getCachePath;

/**
 * 图片加载 缓存
 * @author LiuTao
 * @date 2017/8/21 0021
 */

public class LoadImageHeapler {
    protected final String TAG = "LoadImageHeapler";
    private final String dirName;
    /**
     * 构造方法，需要传入一个保存文件的名字
     */
    public LoadImageHeapler(String dirName) {
        this.dirName = dirName;
    }
    public void loadUserHead(ImageLoadListener listener) {
            if (readFromSDCard(dirName) != null) {
                Bitmap bitmap = readFromSDCard(dirName);
                LogUtil.e(TAG, "从SD卡中加载");
                //返回
                listener.loadImage(bitmap);//返回一个数据给调用者
            } else {
                LogUtil.e(TAG, "从网络中下载");
                requestHead(listener);
            }
    }
    private void requestHead(ImageLoadListener listener) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", MeManager.getUid());
            jsonObject.put("token", MeManager.getToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils
                .postString()
                .url(NetConfig.HOST + GET_HEAD)
                .content(String.valueOf(jsonObject))
                .mediaType(NetConfig.JSON)
                .build()
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showToast(R.string.request_failed);
                    }

                    @Override
                    public void onResponse(Bitmap bitmap, int id) {
                        if (bitmap != null) {
                            listener.loadImage(bitmap);
                            FileUtils.saveToSDCard(dirName, bitmap);
                        } else {
                            ToastUtils.showToast(R.string.request_failed);
                        }
                    }
                });

    }

    /**
     * 定义一个接口
     */
    public interface ImageLoadListener {
        void loadImage(Bitmap bmp);
    }
    /**
     * 文件的读取，
     */
    public Bitmap readFromSDCard(String key) {
        return BitmapFactory.decodeFile(new File(getCachePath(App.get()), key).getAbsolutePath());
    }
}
