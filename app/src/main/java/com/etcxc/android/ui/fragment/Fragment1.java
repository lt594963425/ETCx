package com.etcxc.android.ui.fragment;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.etcxc.android.BuildConfig;
import com.etcxc.android.R;
import com.etcxc.android.ui.activity.ETCIssueActivity;
import com.etcxc.android.utils.PermissionUtil;
import com.etcxc.android.utils.RxUtil;
import com.etcxc.android.utils.SystemUtil;
import com.etcxc.android.utils.ToastUtils;
import com.trello.rxlifecycle2.components.support.RxFragment;

import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by 刘涛 on 2017/6/2 0002.
 */

public class Fragment1 extends RxFragment implements View.OnClickListener {
    private TextView textView1;
    private TextView textView2,mIssueTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item1, null);
        textView1 = (TextView) view.findViewById(R.id.textView1);
        textView2 = (TextView) view.findViewById(R.id.textView2);
        mIssueTextView = (TextView) view.findViewById(R.id.issue_textview);
        initView();
        return view;
    }

    private void initView() {
        textView1.setOnClickListener(this);
        textView2.setOnClickListener(this);
        mIssueTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView1: //注册
//                ToastUtils.showToast("注册....");
                checkVersion();
                break;
            case R.id.textView2: //激活
                requestPermiss();
                break;
            case R.id.issue_textview:
                startActivity(new Intent(getActivity(), ETCIssueActivity.class));
                break;
        }
    }

    private void requestPermiss() {
        PermissionUtil.requestPermissions(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionUtil.OnRequestPermissionsResultCallback() {
            @Override
            public void onRequestPermissionsResult(String[] permissions, int[] grantResults) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    ToastUtils.showToast("申请到了读权限");
            }
        });
    }


    private void checkVersion() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                String versionName = SystemUtil.getVersionName4CheckUpdate();
                int code = BuildConfig.VERSION_CODE;
//            e.onNext(OkClient.get());
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
                .compose(RxUtil.fragmentLifecycle(this))
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
        final AlertDialog.Builder builer = new AlertDialog.Builder(getActivity());
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
                //TODO: 2017/6/15下载，进度栏更新
                //正在下载了，不重复下载
                //下载完了，按钮变成更新
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
}
