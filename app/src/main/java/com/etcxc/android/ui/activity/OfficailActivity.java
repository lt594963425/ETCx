package com.etcxc.android.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.utils.LogUtil;

/**
 * Created by 刘涛 on 2017/6/3 0003.
 */

public class OfficailActivity extends BaseActivity {
    String url = "http://www.xckjetc.com/";
    protected final String TAG = ((Object) this).getClass().getSimpleName();
    private WebView webView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_official);
        webView = (WebView) findViewById(R.id.webView_off);
        WebSettings settings = webView.getSettings();
        webView.loadUrl(url);
        LogUtil.i(TAG, "----------加载网页......----------");
        settings.setJavaScriptEnabled(true);
        // webView.loadUrl("http://baidu.com");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }
    
}
