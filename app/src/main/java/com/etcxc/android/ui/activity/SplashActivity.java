package com.etcxc.android.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.etcxc.android.R;

import java.lang.ref.WeakReference;

/**
 * $name
 *
 * @author ${LiuTao}
 * @date 2017/11/8/008
 */
public class SplashActivity extends Activity {

    private Handler mHandler;
    private Runnable mRunnable;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        mHandler = new SplashHandle(new WeakReference<Context>(this));
        mRunnable = new SplashRunnable();
        mHandler.postDelayed(mRunnable,2000);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private  class SplashHandle extends Handler {
        private Context mContext;

        public SplashHandle(WeakReference<Context> weakReference) {
            mContext = weakReference.get();
        }

    }
    private  class SplashRunnable implements Runnable {
        @Override
        public void run() {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();

        }
    }

}
