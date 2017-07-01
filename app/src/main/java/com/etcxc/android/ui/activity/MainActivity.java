package com.etcxc.android.ui.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.etcxc.android.BuildConfig;
import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.net.download.DownloadConfig1;
import com.etcxc.android.net.download.DownloadManger;
import com.etcxc.android.net.download.DownloadOptions;
import com.etcxc.android.ui.adapter.MyFragmentAdapter;
import com.etcxc.android.ui.fragment.FragmentExpand;
import com.etcxc.android.ui.fragment.FragmentHome;
import com.etcxc.android.ui.fragment.FragmentMine;
import com.etcxc.android.utils.LogUtil;
import com.etcxc.android.utils.PermissionUtil;
import com.etcxc.android.utils.RxUtil;
import com.etcxc.android.utils.SystemUtil;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * 主界面Activity
 * Created by 刘涛 on 2017/6/3 0003.
 */

public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener, TabHost.OnTabChangeListener {
    private ViewPager mViewPager;
    private FragmentTabHost mTabHost;
    private static  final int LOGOUT = 1;//退出

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initPage();
    }

    private void initView() {
        setToolbarBack(false);
        getToolbar().setBackgroundColor(getResources().getColor(R.color.textcolor));
        mViewPager = find(R.id.pager);
        mViewPager.addOnPageChangeListener(this);
        //让ViewPager切换到第1个页面
        mViewPager.setCurrentItem(0, false);
        mTabHost = find(android.R.id.tabhost);
        mViewPager.setOffscreenPageLimit(3);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.pager);
        mTabHost.setOnTabChangedListener(this);
        initTabs();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initTabs() {
        Class mFragmentArray[] = {FragmentHome.class, FragmentExpand.class, FragmentMine.class};
        int mImageViewArray[] = {R.drawable.tab_home_btn, R.drawable.tab_expand_btn, R.drawable.tab_mine_btn};
        String mTextViewArray[] = {getString(R.string.index_home), getString(R.string.index_expand), getString(R.string.mime)};
        int count = mTextViewArray.length;
        for (int i = 0; i < count; i++) {
            TextView textView = new TextView(this);
           // textView.setBackground(getDrawable(R.drawable.tab_etc_textcolor));
            textView.setText(mTextViewArray[i]);
            Drawable d = ContextCompat.getDrawable(this,mImageViewArray[i]);
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            textView.setCompoundDrawables(null, d, null, null);
            textView.setCompoundDrawablePadding(UIUtils.dip2Px(8));
            textView.setPadding(0, UIUtils.dip2Px(8), 0, UIUtils.dip2Px(8));
            textView.setGravity(Gravity.CENTER);
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextViewArray[i]).setIndicator(textView);
            mTabHost.addTab(tabSpec, mFragmentArray[i], null);
        }
    }

    FragmentHome f1;
    FragmentExpand f2;
    FragmentMine f3;

    private void initPage() {
        ArrayList<Fragment> list = new ArrayList();
        f1 = new FragmentHome();
        f2 = new FragmentExpand();
        f3 = new FragmentMine();
        list.add(f1);
        list.add(f2);
        list.add(f3);
        //绑定Fragment适配器
        mViewPager.setAdapter(new MyFragmentAdapter(getSupportFragmentManager(), list));
        mTabHost.getTabWidget().setDividerDrawable(null);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        TabWidget widget = mTabHost.getTabWidget();
        int oldFocusability = widget.getDescendantFocusability();
        widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);//设置View覆盖子类控件而直接获得焦点
        mTabHost.setCurrentTab(position);//根据位置Postion设置当前的Tab
        widget.setDescendantFocusability(oldFocusability);//设置取消分割线
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    @Override
    public void onTabChanged(String tabId) {
        int position = mTabHost.getCurrentTab();
        mViewPager.setCurrentItem(position, false);
        switch (position) {
            case 0:
                setTitle(R.string.app_name);
                break;
            case 1:
                setTitle(R.string.expand);
                break;
            case 2:
                setTitle(R.string.mime);
                break;
        }
    }

    private void checkVersion() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                String versionName = SystemUtil.getVersionName4CheckUpdate();
                int code = BuildConfig.VERSION_CODE;
                e.onNext("{\n" +
                        "    \"code\": \"S_OK\",\n" +
                        "    \"var\": {\n" +
                        "        \"latestVersion\": {\n" +
                        "            \"version\": \"2.4.2.6\",\n" +
                        "            \"download_url\": \"https://s3.static.lunkr.cn/cab/publish/Lunkr4Android/Lunkr_v2.4.2.6_20170605.apk\",\n" +
                        "            \"description\": \"1.新增：邮件召回功能\\n2.新增：解锁加密邮件和文件\\n3.新增：外部信息可分享至论客\\n4.新增：登录日志查询和新版本提示入口\\n5.优化：文件助手，成员列表，新建讨论优化\\n6.优化：部分界面UI&UE优化（如邀请，回执信息,二次验证功能)\"\n" +
                        "        },\n" +
                        "        \"forceUpdate\": false\n" +
                        "    }\n" +
                        "}");
                e.onComplete();
            }
        }).compose(RxUtil.io())
                .compose(RxUtil.activityLifecycle(this))
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        if (TextUtils.isEmpty(s)) return;
                        JSONObject jsonObject = new JSONObject(s);
                        if ("S_OK".equals(jsonObject.getString("code"))) {
                            jsonObject = jsonObject.getJSONObject("var");
                            if (jsonObject == null) return;
                            boolean focrceUpdate = jsonObject.getBoolean("forceUpdate");
                            jsonObject = jsonObject.getJSONObject("latestVersion");
                            if (jsonObject == null) return;
                            String versionName = jsonObject.getString("version");
                            String downloadUrl = jsonObject.getString("download_url");
                            String description = jsonObject.getString("description");
                            showVersionUpdate(focrceUpdate, versionName, downloadUrl, description);
                        }
                    }
                });
    }

    private void showVersionUpdate(final boolean forceUpdate, String versionName, String download_url, String description) {
        final AlertDialog.Builder builer = new AlertDialog.Builder(this);
        String title = getString(R.string.hava_new_version);
        if (!TextUtils.isEmpty(versionName)) title = title + ":" + versionName;
        builer.setTitle(title);
        if (!TextUtils.isEmpty(description)) builer.setMessage(description.replace("\\n", "\n"));
        final AlertDialog d = builer.setPositiveButton(R.string.download, null).setNegativeButton(R.string.cancle, null).setCancelable(false).create();
        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                setDialogListener(d, forceUpdate);
            }
        });
        d.show();
    }

    private void setDialogListener(final AlertDialog d, final boolean forceUpdate) {
        Button positionButton = d.getButton(AlertDialog.BUTTON_POSITIVE);
        positionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!forceUpdate) d.dismiss();
                PermissionUtil.requestPermissions(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionUtil.OnRequestPermissionsResultCallback() {
                    @Override
                    public void onRequestPermissionsResult(String[] permissions, int[] grantResults) {
                        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) return;
                        DownloadOptions options = new DownloadOptions();
                        options.url = mApkUrl;
                        options.targetPath = mDownloadPath;
                        DownloadManger.download(options);
                    }
                });
            }
        });
        if (!forceUpdate) {
            Button negativeButton = d.getButton(AlertDialog.BUTTON_NEGATIVE);
            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.dismiss();
                }
            });
        }
    }

    private ProgressDialog pd;
//    private String mApkUrl = "https://s3.static.lunkr.cn/cab/publish/Lunkr4Android/Lunkr_v2.4.2.6_20170605.apk";
//    private String mApkUrl = "http://az2-ww1.newasp.net/apk/wechat_azb.apk";
    private String mApkUrl = "http://p.gdown.baidu.com/751e9026fe9f467de642cece610464e8706f9c19e5ca2fa00bc27b4c5510a6b441266cb230f65832f43e53921261f648043f8dfa2fd2a44be993a1e523c1511684f722801d18335cd0aed2a21c56eaf4758267ef54b420beb321320b932f6b4b890bb617e88dc6fd20d4e302bd2c8be8c2cdd15e6bcd390ed98573f1324de5a47a80c751b517148892e0f71201b69d0e73eb0fbd97cdcc6fd67adf4fe8c8906684c24914a3106ae7dbaea5938632820bc1250aa4f4faf971a0fdc66fa8a5357b86ba3084752a085fbec0fa72629015124812e6cad4818969db9cfec0fb7c7b81fd9b1828ac21eea93214dbd5fd8ac54cccb8f35f19634db8aff35c73d6e29a63";
    private String mDownloadPath = SystemUtil.downloadDir() + File.separator + "lunkr.apk";

    private void initPd() {
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage(getString(R.string.download));
        pd.setCancelable(false);
        pd.setProgressNumberFormat("%1d kb/%2d kb");
        pd.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancle), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DownloadManger.cancle(mApkUrl);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DownloadOptions options) {
        if (options == null) return;
        if (pd == null) initPd();
        switch (options.step) {
            case DownloadConfig1.STEP_START:
                pd.setMax((int) options.total / 1024);
                pd.show();
                break;
            case DownloadConfig1.STEP_PROGRESS:
                pd.setProgress((int) options.finished / 1024);
                break;
            case DownloadConfig1.STEP_SUCCEED:
                pd.dismiss();
                //安装
                ToastUtils.showToast("下载成功");
                File file = new File(mDownloadPath);
                if (file.exists()) {
                    SystemUtil.installApk(this, file);
                    ToastUtils.showToast("文件大小： " + file.length());
                }
                break;
            case DownloadConfig1.STEP_FAILED:
                pd.dismiss();
                if (options.reason == DownloadConfig1.REASON_NET_FAILED)
                    ToastUtils.showToast(getString(R.string.net_erro));
                else if (options.reason == DownloadConfig1.REASON_CANCELED)
                    ToastUtils.showToast(getString(R.string.cancle));
                break;
        }
        LogUtil.e(TAG, options.toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        f3.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}

