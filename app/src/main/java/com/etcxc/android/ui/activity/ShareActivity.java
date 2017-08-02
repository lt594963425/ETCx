package com.etcxc.android.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.etcxc.android.R;
import com.etcxc.android.base.App;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.WXShareUtils;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.analytics.MobclickAgent;

import static com.etcxc.android.base.Constants.QQ_APP_ID;

/**
 * 推荐好友  （微信好友、微信朋友圈、QQ、短信）分享
 * Created by caoyu on 2017/7/14.
 * */
public class ShareActivity extends BaseActivity implements View.OnClickListener {
    private Button btn_share;
    private TextView tv_wechat, tv_wechat_timeline, tv_qq, tv_sms;

    private BaseUiListener mIUiListener;
    private Toolbar share_toolbar;

    private static String SHARE_URL = "http://www.xckjetc.com/";
    Dialog dialog;
    public static Tencent mTencent;// 新建Tencent实例用于调用分享方法
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        mIUiListener = new BaseUiListener();
        initView();
        mTencent = Tencent.createInstance(QQ_APP_ID,this);
    }

    private void initView() {
        share_toolbar = (Toolbar) findViewById(R.id.share_toolbar);
        btn_share = (Button) findViewById(R.id.btn_share);

        setSupportActionBar(share_toolbar);
        share_toolbar.setTitle(R.string.my_share);
        share_toolbar.inflateMenu(R.menu.menu_share);
        setBarBack(share_toolbar);
        btn_share.setOnClickListener(this);
        share_toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_share){
                    showShareDialog();
                }
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_share://分享按钮
                showShareDialog();
                break;
            case R.id.tv_wechat:
                String content = getString(R.string.sharecontent) + SHARE_URL;
                WXShareUtils.getInstance().doTextSend(App.get(), content, "迅畅在线", 0);
                dialog.dismiss();
                break;
            case R.id.tv_wechat_timeline:
                String content1 = getString(R.string.sharecontent) + SHARE_URL;
                WXShareUtils.getInstance().doTextSend(App.get(), content1, "迅畅在线", 1);
                dialog.dismiss();
                break;
            case R.id.tv_qq:
                MobclickAgent.onEvent(this, "QQShare" );
                shareToQQ();
                dialog.dismiss();
                break;
            case R.id.tv_sms:
                MobclickAgent.onEvent(this, "SMSShare" );
                String smsBody = "我正在浏览这个,觉得真不错,推荐给你哦~ 地址:" + SHARE_URL;
                sendSMS(SHARE_URL, smsBody);
                dialog.dismiss();
                break;
        }

    }

    private void showShareDialog() {
        dialog = new Dialog(this, R.style.BottomDialog);
        //填充对话框的布局
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_share, null);
        //初始化控件
        initDialogView(inflate);
        //将布局设置给Dialog
        dialog.setContentView(inflate);
        dialog.setCancelable(true);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        int mWindowWidth;
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        mWindowWidth = displayMetrics.widthPixels;
        dialog.setContentView(inflate, new ViewGroup.MarginLayoutParams(mWindowWidth,
                ViewGroup.MarginLayoutParams.MATCH_PARENT));
        dialog.show();//显示对话框
    }

    private void initDialogView(View v) {
        tv_wechat = (TextView) v.findViewById(R.id.tv_wechat);
        tv_wechat_timeline = (TextView) v.findViewById(R.id.tv_wechat_timeline);
        tv_qq = (TextView) v.findViewById(R.id.tv_qq);
        tv_sms = (TextView) v.findViewById(R.id.tv_sms);

        tv_wechat.setOnClickListener(this);
        tv_wechat_timeline.setOnClickListener(this);
        tv_qq.setOnClickListener(this);
        tv_sms.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Tencent.onActivityResultData(requestCode, resultCode, data, mIUiListener);
        if (requestCode == Constants.REQUEST_API) {
            if (requestCode == Constants.REQUEST_QQ_SHARE || resultCode == Constants.REQUEST_OLD_SHARE) {
                Tencent.handleResultData(data, mIUiListener);
            }
        }
    }

    /**
     * QQ分享回调监听
     */
    private class BaseUiListener implements IUiListener {

        @Override

        public void onError(UiError e) {
            ToastUtils.showToast(getString(R.string.share_error));
        }

        @Override
        public void onComplete(Object o) {
            ToastUtils.showToast(getString(R.string.share_complete));
        }


        @Override
        public void onCancel() {
            ToastUtils.showToast(getString(R.string.share_cancel));
        }

    }

    private Bundle bundle;
    /**
     * qq分享
     */
    private void shareToQQ() {
        bundle = new Bundle();
        //这条分享消息被好友点击后的跳转URL。
        bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, SHARE_URL);
        //分享的标题。注：PARAM_TITLE、PARAM_IMAGE_URL、PARAM_SUMMARY不能全为空，最少必须有一个是有值的。
        bundle.putString(QQShare.SHARE_TO_QQ_TITLE, "迅畅在线");
        //分享的消息摘要，最长50个字
        bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, "我正在浏览这个,觉得真不错,推荐给你哦~");
        //手Q客户端顶部，替换“返回”按钮文字，如果为空，用返回代替
        bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, "我正在浏览这个,觉得真不错,推荐给你哦~");
        mTencent.shareToQQ(this, bundle, mIUiListener);
    }

    /**
     * 发短信
     */
    private void sendSMS(String webUrl, String smsBody) {
        Uri smsToUri = Uri.parse("smsto:");
        Intent sendIntent = new Intent(Intent.ACTION_VIEW, smsToUri);
        sendIntent.putExtra("sms_body", smsBody + webUrl);
        sendIntent.setType("vnd.android-dir/mms-sms");
        startActivityForResult(sendIntent, 1002);
    }

    private void setBarBack(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
