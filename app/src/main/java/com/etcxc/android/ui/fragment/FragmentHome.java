package com.etcxc.android.ui.fragment;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.etcxc.android.BuildConfig;
import com.etcxc.android.R;
import com.etcxc.android.base.App;
import com.etcxc.android.base.BaseFragment;
import com.etcxc.android.ui.activity.ETCIssueActivity;
import com.etcxc.android.ui.activity.MainActivity;
import com.etcxc.android.ui.adapter.GlideImageLoader;
import com.etcxc.android.ui.adapter.MyGridViewAdapter;
import com.etcxc.android.ui.view.FocusTextview;
import com.etcxc.android.utils.PermissionUtil;
import com.etcxc.android.utils.RxUtil;
import com.etcxc.android.utils.SystemUtil;
import com.etcxc.android.utils.ToastUtils;
import com.youth.banner.Banner;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by 刘涛 on 2017/6/2 0002.
 */

public class FragmentHome extends BaseFragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    String[] imagess = new String[]{"http://pic29.photophoto.cn/20131125/0022005500418920_b.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1498558030884&di=b10f693abcebd09dfb309d89702672e5&imgtype=0&src=http%3A%2F%2Fpic29.nipic.com%2F20130511%2F12011435_141504339147_2.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1498558204252&di=8a6ce8463360d42b7518665a469391fc&imgtype=0&src=http%3A%2F%2Fpic.58pic.com%2F58pic%2F11%2F04%2F37%2F04658PICQHc.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1498558224830&di=b546d2811f9fa910decc55b981f8df8c&imgtype=0&src=http%3A%2F%2Fpic2.ooopic.com%2F11%2F77%2F47%2F63bOOOPIC74_1024.jpg"};
    private GridView mHomeGV;
    private ViewPager mVPger;
    private TextView mETCRecharge, mETCSave, mETCDetile;
    private View mETCOnline;
    private FocusTextview ft;
    private static int[] image = {
            R.drawable.vd_brief_description_of_business,
            R.drawable.vd_recharge_record,
            R.drawable.vd_through_the_detail,
            R.drawable.vd_activate,
            R.drawable.vd_complaint_and_advice,
            R.drawable.vd_gridchek,};
    private Integer [] imageResIDs = {
            R.mipmap.advinfo,
            R.mipmap.advinfo_one,
            R.mipmap.advinfo_two};
    private  String[] title = {
            App.get().getString(R.string.bussiness),App.get().getString(R.string.rechargerecord)
            ,App.get().getString(R.string.pass_detail), App.get().getString(R.string.activate),App.get().getString(R.string.advice),
            App.get().getString(R.string.gridchek)};
    private String strDitle ="高速公路畅通无阻\n“0”元照进不误";
    private MainActivity mActivity;
    private Banner banner;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = (MainActivity) getActivity();
        View view = inflater.inflate(R.layout.fragment_home, null);
        ft = (FocusTextview) view.findViewById(R.id.ft_tv);
        ft.setEllipsize(android.text.TextUtils.TruncateAt.MARQUEE);
        mETCOnline = view.findViewById(R.id.home_etcmore_llayout);//ETC在线办理
        mETCRecharge = (TextView) view.findViewById(R.id.home_etcrecharge_tv);//ETC充值
        mETCSave = (TextView) view.findViewById(R.id.home_etccirclesave_tv);//ETC圈存
        mETCDetile= (TextView) view.findViewById(R.id.home_detile_tv);//0元照进不误
        mHomeGV = (GridView) view.findViewById(R.id.home_gridview);
        //轮播图
        banner = (Banner) view.findViewById(R.id.home_banner);
        banner.setImages(new ArrayList<>(Arrays.asList(imagess))).setImageLoader(new GlideImageLoader()).start();
        initView();
        return view;
    }
    private void initView() {
        mETCOnline.setOnClickListener(this);
        SpannableStringBuilder style=new SpannableStringBuilder(strDitle);
        style.setSpan(new TextAppearanceSpan(mActivity, R.style.style0), 0, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(new TextAppearanceSpan(mActivity, R.style.style1), 10, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(new TextAppearanceSpan(mActivity, R.style.style0), 11, 17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mETCDetile.setText(style);//将其添加到tv中
        mHomeGV.setAdapter(new MyGridViewAdapter(image,title,getActivity()));
        mHomeGV.setOnItemClickListener(this);
    }

    @Override
    public void onStart() {

        super.onStart();
    }

    /**
     * GridView
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:   //业务办理
                break;
            case 1:   //充值记录
                break;
            case 2:   //进行通信
                break;
            case 3:   //预约激活
                break;
            case 4:   //投诉建议
                break;
            case 5:   //网点查询
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_etcmore_llayout:
                startActivity(new Intent(mActivity, ETCIssueActivity.class));
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

    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

    }
}
