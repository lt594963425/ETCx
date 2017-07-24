package com.etcxc.android.ui.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.etcxc.android.BuildConfig;
import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.net.NetConfig;
import com.etcxc.android.net.OkClient;
import com.etcxc.android.net.download.DownloadConfig1;
import com.etcxc.android.net.download.DownloadManger;
import com.etcxc.android.net.download.DownloadOptions;
import com.etcxc.android.utils.LogUtil;
import com.etcxc.android.utils.PermissionUtil;
import com.etcxc.android.utils.RxUtil;
import com.etcxc.android.utils.SystemUtil;
import com.etcxc.android.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import static com.etcxc.android.R.string.download;
import static com.etcxc.android.net.OkClient.HTTP_PREFIX;

/**
 * 关于我们
 * Created by xwpeng on 2017/6/30.
 */

public class AboutUsActivity extends BaseActivity implements View.OnClickListener {
    private TextView mVersionCodeTextView, mCheckUpdateTextView;
    private ProgressDialog pd;
    private String mApkUrl;
    private final static String DOWNLOAD_FILE_PATH = SystemUtil.downloadDir() + File.separator + "xcetc.apk";
    private final static String FUNC = "/version/version_manage/versionmanage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        initView();
    }

    public void initView() {
        setTitle(R.string.about_us);
        mVersionCodeTextView = find(R.id.about_us_versioncode);
        mCheckUpdateTextView = find(R.id.about_us_check_update);
        mCheckUpdateTextView.setOnClickListener(this);

        String versionName = BuildConfig.VERSION_NAME;
        versionName = versionName.substring(0, versionName.lastIndexOf("."));
        versionName = versionName.substring(0, versionName.lastIndexOf("."));
        versionName = versionName.substring(0, versionName.lastIndexOf("."));
        mVersionCodeTextView.setText(String.valueOf("V" + versionName));

        find(R.id.about_us_test_json_api).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.about_us_check_update:
                Map<String, String> params = new HashMap<>();
                params.put("version_code", String.valueOf(BuildConfig.VERSION_CODE));
                checkVersion(NetConfig.consistUrl(FUNC, params));
                break;
            case R.id.about_us_test_json_api:
                startActivity(new Intent(this, TestJsonApiActivity.class));
                break;
        }
    }

    private void checkVersion(String url) {
        showProgressDialog(R.string.loading);
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                e.onNext(OkClient.get(url, new JSONObject()));
                e.onComplete();
            }
        }).compose(RxUtil.io())
                .compose(RxUtil.activityLifecycle(this))
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        closeProgressDialog();
                        if (TextUtils.isEmpty(s)) return;
                        JSONObject jsonObject = new JSONObject(s);
                        if ("s_ok".equals(jsonObject.getString("code"))) {
                            jsonObject = jsonObject.getJSONObject("var");
                            if (jsonObject == null) return;
                            boolean focrceUpdate = jsonObject.getBoolean("force");
                            String versionName = jsonObject.getString("version_num");
                            mApkUrl = HTTP_PREFIX + jsonObject.getString("version_url");
                            String description = jsonObject.getString("version_content");
                            showVersionUpdate(focrceUpdate, versionName, mApkUrl, description);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        LogUtil.e(TAG, "checkVersion", throwable);
                        ToastUtils.showToast(R.string.request_failed);
                        closeProgressDialog();
                    }
                });
    }

    private void showVersionUpdate(final boolean forceUpdate, String versionName, String download_url, String description) {
        final AlertDialog.Builder builer = new AlertDialog.Builder(this);
        String title = getString(R.string.hava_new_version);
        if (!TextUtils.isEmpty(versionName)) title = title + ":" + versionName;
        builer.setTitle(title);
        if (!TextUtils.isEmpty(description)) builer.setMessage(description.replace("\\n", "\n"));
        final AlertDialog d = builer.setPositiveButton(download, null)
                .setNegativeButton(R.string.cancle, null)
                .setCancelable(false)
                .create();
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
                download();
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

    private void download() {
        PermissionUtil.requestPermissions(AboutUsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionUtil.OnRequestPermissionsResultCallback() {
            @Override
            public void onRequestPermissionsResult(String[] permissions, int[] grantResults) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) return;
                DownloadOptions options = new DownloadOptions();
                options.url = mApkUrl;
                options.targetPath = DOWNLOAD_FILE_PATH;
                DownloadManger.download(options);
            }
        });
    }

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
                ToastUtils.showToast(R.string.download_success);
                File file = new File(DOWNLOAD_FILE_PATH);
                if (file.exists()) SystemUtil.installApk(this, file);
                break;
            case DownloadConfig1.STEP_FAILED:
                pd.dismiss();
                if (options.reason == DownloadConfig1.REASON_NET_FAILED)
                    ToastUtils.showToast(getString(R.string.net_erro));
                else if (options.reason == DownloadConfig1.REASON_CANCELED)
                    ToastUtils.showToast(getString(R.string.download_cancle));
                break;
        }
    }
}
