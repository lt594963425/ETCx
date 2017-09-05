package com.etcxc.android.helper;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.etcxc.android.BuildConfig;
import com.etcxc.android.R;
import com.etcxc.android.bean.VersionCheckInfo;
import com.etcxc.android.modle.sp.PublicSPUtil;
import com.etcxc.android.net.NetConfig;
import com.etcxc.android.net.OkHttpUtils;
import com.etcxc.android.net.download.DownloadConfig1;
import com.etcxc.android.net.download.DownloadManger;
import com.etcxc.android.net.download.DownloadOptions;
import com.etcxc.android.utils.LogUtil;
import com.etcxc.android.utils.PermissionUtil;
import com.etcxc.android.utils.RxUtil;
import com.etcxc.android.utils.SystemUtil;
import com.etcxc.android.utils.ToastUtils;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import org.json.JSONObject;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import static com.etcxc.android.R.string.download;
import static com.etcxc.android.net.FUNC.VERSION_FUNC;
import static com.etcxc.android.utils.UIUtils.getString;

/**
 * 版本更新辅助
 * Created by xwpeng on 2017/7/26.
 */

public class VersionUpdateHelper {
    private final static String TAG = VersionUpdateHelper.class.getSimpleName();
    private VersionCheckInfo mVersionInfo;
    private RxAppCompatActivity mActivity;
    private ProgressDialog mPd;
//    private final static String FUNC = "/xczx/version_manage/versionmanage";

    public VersionUpdateHelper(RxAppCompatActivity mActivity) {
        this.mActivity = mActivity;
    }

    public void checkVersion() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("version_code", String.valueOf(BuildConfig.VERSION_CODE));

                e.onNext(OkHttpUtils
                        .postString()
                        .url(NetConfig.HOST + VERSION_FUNC)
                        .content(String.valueOf(jsonObject))
                        .mediaType(NetConfig.JSON)
                        .build()
                        .execute().body().string());
                e.onComplete();
            }
        }).compose(RxUtil.io())
                .compose(RxUtil.activityLifecycle(mActivity))
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        if (TextUtils.isEmpty(s)) return;
                        JSONObject jsonObject = new JSONObject(s);
                        if ("s_ok".equals(jsonObject.getString("code"))) {
                            jsonObject = jsonObject.getJSONObject("var");
                            if (jsonObject == null) return;
                            showVersionUpdate(VersionCheckInfo.parse(jsonObject));
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        LogUtil.e(TAG, "checkVersion", throwable);
                        ToastUtils.showToast(R.string.request_failed);
                    }
                });
    }

    private void showVersionUpdate(VersionCheckInfo info) {
        info.focrceUpdate = false;//todo : 方便测试，正式环境去掉
        mVersionInfo = info;
        PublicSPUtil.getInstance().putInt("check_version_code", 4);//TODO: 2017/7/26 需要后端返回version_code,方便判断，现在固定值4
        final AlertDialog.Builder builer = new AlertDialog.Builder(mActivity);
        String title = getString(R.string.hava_new_version);
        if (!TextUtils.isEmpty(info.versionName)) title = title + ":" + info.versionName;
        builer.setTitle(title);
        if (!TextUtils.isEmpty(info.description))
            builer.setMessage(info.description.replace("\\n", "\n"));
        builer.setPositiveButton(download, null).setCancelable(false);
        if (!info.focrceUpdate) builer.setNegativeButton(R.string.cancle, null);
        AlertDialog d = builer.create();
        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                setDialogListener(d, info.focrceUpdate);
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
        PermissionUtil.requestPermissions(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionUtil.OnRequestPermissionsResultCallback() {
            @Override
            public void onRequestPermissionsResult(String[] permissions, int[] grantResults) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) return;
                File file = new File(mVersionInfo.apkUrl);
                if (file.exists()) {
                    SystemUtil.installApk(mActivity, file);
                    return;
                }
                DownloadOptions options = new DownloadOptions();
                options.url = mVersionInfo.apkUrl;
                options.targetPath = mVersionInfo.downloadPath;
                DownloadManger.download(options);
            }
        });
    }

    private void initPd() {
        mPd = new ProgressDialog(mActivity);
        mPd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mPd.setMessage(getString(download));
        mPd.setCancelable(false);
        mPd.setProgressNumberFormat("%1d kb/%2d kb");
        mPd.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancle), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DownloadManger.cancle(mVersionInfo.apkUrl);
            }
        });
    }

    public void downloadPd(DownloadOptions options) {
        if (options == null) return;
        if (mPd == null) initPd();
        switch (options.step) {
            case DownloadConfig1.STEP_START:
                mPd.setMax((int) options.total / 1024);
                mPd.show();
                break;
            case DownloadConfig1.STEP_PROGRESS:
                mPd.setProgress((int) options.finished / 1024);
                break;
            case DownloadConfig1.STEP_SUCCEED:
                mPd.dismiss();
                ToastUtils.showToast(R.string.download_success);
                File file = new File(mVersionInfo.downloadPath);
                if (file.exists()) SystemUtil.installApk(mActivity, file);
                break;
            case DownloadConfig1.STEP_FAILED:
                mPd.dismiss();
                if (options.reason == DownloadConfig1.REASON_NET_FAILED)
                    ToastUtils.showToast(getString(R.string.net_erro));
                else if (options.reason == DownloadConfig1.REASON_CANCELED)
                    ToastUtils.showToast(getString(R.string.download_cancle));
                break;
        }
    }
}
