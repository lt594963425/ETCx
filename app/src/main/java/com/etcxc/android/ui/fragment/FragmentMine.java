package com.etcxc.android.ui.fragment;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.etcxc.MeManager;
import com.etcxc.android.BuildConfig;
import com.etcxc.android.R;
import com.etcxc.android.base.BaseFragment;
import com.etcxc.android.modle.sp.PublicSPUtil;
import com.etcxc.android.net.NetConfig;
import com.etcxc.android.net.OkHttpUtils;
import com.etcxc.android.ui.activity.AboutUsActivity;
import com.etcxc.android.ui.activity.ChangePasswordActivity;
import com.etcxc.android.ui.activity.ChangePhoneActivity;
import com.etcxc.android.ui.activity.LoginActivity;
import com.etcxc.android.ui.activity.MineCardActivity;
import com.etcxc.android.ui.activity.PersonalInfoActivity;
import com.etcxc.android.ui.activity.ReceiptAddressActivity;
import com.etcxc.android.ui.activity.ShareActivity;
import com.etcxc.android.ui.proxy.ThreadPoolProxyFactory;
import com.etcxc.android.ui.view.CircleImageView;
import com.etcxc.android.ui.view.ColorCircle;
import com.etcxc.android.utils.FileUtils;
import com.etcxc.android.utils.LoadImageHeapler;
import com.etcxc.android.utils.LogUtil;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.UIUtils;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.etcxc.android.R.id.mine_user_head;
import static com.etcxc.android.net.FUNC.LOGIN_OUT;
import static com.etcxc.android.net.NetConfig.JSON;


/**
 * 我的
 *
 * @author Liutao
 * @date 2017/6/2 0002
 */
public class FragmentMine extends BaseFragment implements View.OnClickListener {
    protected final String TAG = "FragmentMine";
    private static final int REQUST_CODE = 1;
    private CircleImageView mMineUserHead;
    private TextView mMineUserName;
    private TextView mExit;
    private String CROP_HEAD;
    private Uri resultUri;
    private LoadImageHeapler mHeadLoader;
    protected String IMAGE_TAG = "MINE_LOAD_IMAGE";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mine, null);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        Resources r = getActivity().getResources();
        resultUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + r.getResourcePackageName(R.drawable.vd_head) + "/"
                + r.getResourceTypeName(R.drawable.vd_head) + "/"
                + r.getResourceEntryName(R.drawable.vd_head));
        find(R.id.mine_my_card_toright).setOnClickListener(this);
        find(R.id.mine_harvestaddress_toright).setOnClickListener(this);
        find(R.id.mine_recommendfriend_toright).setOnClickListener(this);
        find(R.id.mine_changepassword_toright).setOnClickListener(this);
        find(R.id.mine_changephone_toright).setOnClickListener(this);
        find(R.id.mine_aboutus_toright).setOnClickListener(this);
        find(R.id.mine_layout).setOnClickListener(this);

        mMineUserHead = find(mine_user_head);
        mMineUserName = find(R.id.mine_user_name);
        mExit = find(R.id.mine_exit_login);
        mExit.setOnClickListener(this);
        mMineUserName.setText(R.string.now_login);
        VectorDrawableCompat drawable = VectorDrawableCompat.create(getResources(), R.drawable.vd_head, null);
        mMineUserHead.setImageDrawable(drawable);
        ColorCircle mUpdateDot = find(R.id.update_dot);
        mUpdateDot.setRadius(UIUtils.dip2Px(5));
        mUpdateDot.setColor(getResources().getColor(R.color.update_dot));
        if (PublicSPUtil.getInstance().getInt("check_version_code", 0) > BuildConfig.VERSION_CODE) {
            mUpdateDot.setVisibility(View.VISIBLE);
        }
        mMineUserHead.setOnClickListener(this);
        mMineUserHead.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                File file = new File(FileUtils.getCachePath(getActivity()), CROP_HEAD);
                if (file.exists()) {
                    resultUri = Uri.fromFile(file);
                }
                FileUtils.showBigImageView(getActivity(), resultUri);
                return false;
            }

        });
        ThreadPoolProxyFactory.getNormalThreadPoolProxy().submit(LOAD_DATA);
    }

    private Runnable LOAD_DATA = new Runnable() {
        @Override
        public void run() {
            if (MeManager.getIsLogin()) {
                mExit.setVisibility(View.VISIBLE);
                mMineUserName.setText(MeManager.getName());
                initUserInfo();
            } else {
                mExit.setVisibility(View.INVISIBLE);
                mMineUserName.setText(R.string.now_login);
                VectorDrawableCompat drawable = VectorDrawableCompat.create(getResources(), R.drawable.vd_head, null);
                mMineUserHead.setImageDrawable(drawable);
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_my_card_toright:
                openActivity(MineCardActivity.class);
                break;
            case R.id.mine_user_head:
            case R.id.mine_layout:
                if (MeManager.getIsLogin()) {
                    openActivityForResult(PersonalInfoActivity.class, REQUST_CODE);

                } else {
                    //未登录：点击修改密码跳入登录页面
                    openActivityForResult(LoginActivity.class, REQUST_CODE);
                }

                break;
            case R.id.mine_harvestaddress_toright:
                if (!MeManager.getIsLogin()) {
                    //未登录：点击修改密码跳入登录页面
                    openActivityForResult(LoginActivity.class, REQUST_CODE);
                    return;
                } else {
                    openActivity(ReceiptAddressActivity.class);
                }
                break;
            case R.id.mine_recommendfriend_toright:
                openActivity(ShareActivity.class);
                break;
            case R.id.mine_changepassword_toright:
                if (!MeManager.getIsLogin()) {
                    //未登录：点击修改密码跳入登录页面
                    openActivityForResult(LoginActivity.class, REQUST_CODE);
                } else {
                    //已登录：点击修改密码进入修改密码页面
                    openActivityForResult(ChangePasswordActivity.class, REQUST_CODE);
                }
                break;
            case R.id.mine_changephone_toright:
                if (!MeManager.getIsLogin()) {
                    //未登录：点击修改密码跳入登录页面
                    openActivityForResult(LoginActivity.class, REQUST_CODE);
                    return;
                } else {
                    openActivity(ChangePhoneActivity.class);
                }
                break;
            case R.id.mine_aboutus_toright:
                openActivity(AboutUsActivity.class);
                break;
            case R.id.mine_exit_login:
                showExitDialog();
                break;
            default:
                break;
        }

    }

    public void initUserInfo() {
        CROP_HEAD = MeManager.getToken() + "_crop.jpg";
        if (mHeadLoader == null) {
            mHeadLoader = new LoadImageHeapler(CROP_HEAD, IMAGE_TAG);
        }
        mHeadLoader.loadUserHead(new LoadImageHeapler.ImageLoadListener() {
            @Override
            public void loadImage(Bitmap bmp) {
                mMineUserHead.setImageBitmap(bmp);
            }
        });
    }

    private void requestLoginOut() {
        JSONObject jsonObject = new JSONObject();
        showProgressDialog(R.string.loading);

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                jsonObject.put("uid", MeManager.getUid())
                        .put("token", MeManager.getToken());
                e.onNext(OkHttpUtils.postString()
                        .url(NetConfig.HOST + LOGIN_OUT)
                        .content(String.valueOf(jsonObject))
                        .mediaType(JSON)
                        .build()
                        .execute().body().string());
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        Log.e(TAG, s);
                        JSONObject result = new JSONObject(s);
                        String code = result.getString("code");
                        if ("s_ok".equals(code)) {
                            closeProgressDialog();
                            ToastUtils.showToast(R.string.exitlogin);
                            MeManager.clearAll();
                            MeManager.setIsLgon(false);
                            ThreadPoolProxyFactory.getNormalThreadPoolProxy().submit(LOAD_DATA);
                        }
                        if ("error".equals(code)) {
                            closeProgressDialog();
                            String msg = result.getString("message");
                            if (NetConfig.ERROR_TOKEN.equals(msg)) {
                                MeManager.setIsLgon(false);
                                openActivityForResult(LoginActivity.class, REQUST_CODE);
                            }
                            if ("auth failed".equals(msg)) {
                                MeManager.setIsLgon(false);
                            }

                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        closeProgressDialog();
                        Log.e(TAG, throwable.toString());
                        ToastUtils.showToast(R.string.request_failed);

                    }
                });
    }

    private void showExitDialog() {
        if (!MeManager.getIsLogin()) {
            return;
        }
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.exit_login, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setCancelable(false);
        final Dialog dialog = builder.show();
        view.findViewById(R.id.dialog_dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.dialog_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                requestLoginOut();
                dialog.dismiss();

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUST_CODE:
                ThreadPoolProxyFactory.getNormalThreadPoolProxy().submit(LOAD_DATA);
                break;
            default:
                break;
        }

    }

    @Override
    public void onResume() {
        LogUtil.e(TAG, "onResume");
        MobclickAgent.onPageStart("FragmentExpand");
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("FragmentExpand");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHeadLoader != null) {
            mHeadLoader.CancleNet(IMAGE_TAG);
            mHeadLoader = null;
        }
    }


}
