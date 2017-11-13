package com.etcxc.android.ui.activity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.model.LottieComposition;
import com.etcxc.android.R;

/**
 * $name
 *
 * @author ${LiuTao}
 * @date 2017/11/8/008
 */
public class SplashActivity extends Activity {

    private LottieAnimationView mSplashAnimation;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        mSplashAnimation = findViewById(R.id.animation_view_click);
        mSplashAnimation.setVisibility(View.VISIBLE);
        LottieComposition.fromAssetFileName(SplashActivity.this, "SplashAnimation.json", new LottieComposition.OnCompositionLoadedListener() {

            @Override
            public void onCompositionLoaded(LottieComposition composition) {
                mSplashAnimation.setComposition(composition);
                mSplashAnimation.setProgress(0.333f);

                mSplashAnimation.playAnimation();
            }
        });

        mSplashAnimation.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int progress = (int) (animation.getAnimatedFraction() * 100);
                if (progress == 60) {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();

         mSplashAnimation.cancelAnimation();
    }

}
