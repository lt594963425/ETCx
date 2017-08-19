package com.etcxc.android.ui.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.modle.sp.PublicSPUtil;
import com.shizhefei.view.largeimage.LargeImageView;

import static com.etcxc.android.utils.FileUtils.getImageDegree;
import static com.etcxc.android.utils.FileUtils.rotateBitmapByDegree;

/**
 * 高清大图显示Activity
 * Created by xwpeng on 2017/6/23.
 */

public class LargeImageActivity extends BaseActivity {
    private LargeImageView mLargeImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_large_image);
        mLargeImageView = find(R.id.preview_image_largeimageview);
        //设置缩放倍数
        mLargeImageView.setCriticalScaleValueHook(new LargeImageView.CriticalScaleValueHook() {
            @Override
            public float getMinScale(LargeImageView largeImageView, int imageWidth, int imageHeight, float suggestMinScale) {
                return 0.5f;
            }

            @Override
            public float getMaxScale(LargeImageView largeImageView, int imageWidth, int imageHeight, float suggestMaxScale) {
                return 6;
            }
        });

        String path = getIntent().getStringExtra("path");
        if (!TextUtils.isEmpty(path)) {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap bitmap = BitmapFactory.decodeFile(path, opt);
            mLargeImageView.setImage(rotateBitmapByDegree(bitmap, getImageDegree(path)));
        }

     }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        PublicSPUtil.getInstance().putBoolean("isScal",false);
        finish();
    }

    @Override
    protected void onPause() {
        if (PublicSPUtil.getInstance().getBoolean("isScal",false)) {
            this.overridePendingTransition(R.anim.anim_out, R.anim.zoom_exit);
        }
        super.onPause();
    }

}
