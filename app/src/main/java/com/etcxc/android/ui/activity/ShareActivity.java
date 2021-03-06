package com.etcxc.android.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

import com.etcxc.android.R;
import com.etcxc.android.base.App;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.ui.view.SharePopupWindows;
import com.etcxc.android.utils.SystemUtil;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.WXShareUtils;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.Method;

import static com.etcxc.android.base.Constants.QQ_APP_ID;

/**
 * 推荐好友  （微信好友、微信朋友圈、QQ、短信）分享
 * Created by caoyu on 2017/7/14.
 */
public class ShareActivity extends BaseActivity implements View.OnClickListener {
    private Button btn_share;
    private BaseUiListener mIUiListener;
    private static String SHARE_URL = "http://www.xckjetc.com/";
    public static Tencent mTencent;// 新建Tencent实例用于调用分享方法
    SharePopupWindows sharePopupWindows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        mIUiListener = new BaseUiListener();
        initView();
        mTencent = Tencent.createInstance(QQ_APP_ID, this);
    }

    private void initView() {
        btn_share = (Button) findViewById(R.id.btn_share);
        getToolbar().inflateMenu(R.menu.menu_share);
        setTitle(R.string.my_share);
        setBarBack();
        btn_share.setOnClickListener(this);
        getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_share) {
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
        }

    }

    private void showShareDialog() {
        sharePopupWindows = new SharePopupWindows(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharePopupWindows.dismiss();
                switch (v.getId()) {
                    case R.id.tv_wechat:
                        String content = getString(R.string.sharecontent) + SHARE_URL;
                        WXShareUtils.getInstance().doTextSend(App.get(), content, "迅畅在线", 0);
                        break;
                    case R.id.tv_wechat_timeline:
                        String content1 = getString(R.string.sharecontent) + SHARE_URL;
                        WXShareUtils.getInstance().doTextSend(App.get(), content1, "迅畅在线", 1);
                        break;
                    case R.id.tv_qq:
                        MobclickAgent.onEvent(ShareActivity.this, "QQShare");
                        if (SystemUtil.isQQClientAvailable(ShareActivity.this)) {
                            shareToQQ();
                        } else {
                            ToastUtils.showToast("未安装QQ");
                        }

                        break;
                    case R.id.tv_sms:
                        MobclickAgent.onEvent(ShareActivity.this, "SMSShare");
                        String smsBody = "我正在浏览这个,觉得真不错,推荐给你哦~ 地址:" + SHARE_URL;
                        sendSMS(SHARE_URL, smsBody);
                        break;
                }
            }
        });

        if (checkDeviceHasNavigationBar(this)) {
            int heigth_tobottom = getNavigationBarHeight();
            sharePopupWindows.showAtLocation(this.findViewById(R.id.share), Gravity.BOTTOM, 0, heigth_tobottom);
        } else {
            sharePopupWindows.showAtLocation(this.findViewById(R.id.share), Gravity.BOTTOM, 0, 0);
        }
        setBackgroundAlpha(0.5f);
        sharePopupWindows.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // popupWindow隐藏时恢复屏幕正常透明度
                setBackgroundAlpha(1.0f);
            }
        });
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
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.putExtra("sms_body", smsBody + webUrl);
        sendIntent.setType("vnd.android-dir/mms-sms");
        startActivityForResult(sendIntent, 1002);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * /获取是否存在虚拟按键 NavigationBar：如果是有就返回true,如果是没有就是返回的false。第二种方法
     */
    private static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
        }
        return hasNavigationBar;
    }

    /**
     * 获取navigationbar的高度。
     */
    private int getNavigationBarHeight() {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    /**
     * 设置添加屏幕的背景透明度 * * @param bgAlpha * 屏幕透明度0.0-1.0 1表示完全不透明
     */
    public void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
    }
}
