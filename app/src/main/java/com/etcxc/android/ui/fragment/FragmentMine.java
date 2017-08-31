package com.etcxc.android.ui.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.etcxc.MeManager;
import com.etcxc.android.BuildConfig;
import com.etcxc.android.R;
import com.etcxc.android.base.BaseFragment;
import com.etcxc.android.modle.sp.PublicSPUtil;
import com.etcxc.android.net.NetConfig;
import com.etcxc.android.net.OkClient;
import com.etcxc.android.ui.activity.AboutUsActivity;
import com.etcxc.android.ui.activity.ChangePasswordActivity;
import com.etcxc.android.ui.activity.ChangePhoneActivity;
import com.etcxc.android.ui.activity.LoginActivity;
import com.etcxc.android.ui.activity.MineCardActivity;
import com.etcxc.android.ui.activity.PersonalInfoActivity;
import com.etcxc.android.ui.activity.ReceiptAddressActivity;
import com.etcxc.android.ui.activity.ShareActivity;
import com.etcxc.android.ui.view.CircleImageView;
import com.etcxc.android.ui.view.ColorCircle;
import com.etcxc.android.utils.FileUtils;
import com.etcxc.android.utils.LoadImageHeapler;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.UIUtils;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.etcxc.android.net.FUNC.LOGIN_OUT;
import static com.etcxc.android.utils.FileUtils.getImageDegree;


/**
 * 我的
 * Created by 刘涛 on 2017/6/2 0002.
 */
public class FragmentMine extends BaseFragment implements View.OnClickListener {
    protected final String TAG = "FragmentMine";
    private static final int REQUST_CODE = 1;
    private CircleImageView mMineUserHead;
    private TextView mMineUserName;
    private TextView mExit;
    private FrameLayout mMinewLauout;
    private Handler mHandler = new Handler();
    private ColorCircle mUpdateDot;
    /*头像名称*/
    private File mFile;
    private Uri uri;
    private String CROP_HEAD = "user_head.jpg";

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
        find(R.id.mine_my_card_toright).setOnClickListener(this);
        find(R.id.mine_harvestaddress_toright).setOnClickListener(this);
        find(R.id.mine_recommendfriend_toright).setOnClickListener(this);
        find(R.id.mine_changepassword_toright).setOnClickListener(this);
        find(R.id.mine_changephone_toright).setOnClickListener(this);
        find(R.id.mine_aboutus_toright).setOnClickListener(this);
        find(R.id.mine_layout).setOnClickListener(this);
        mMineUserHead = find(R.id.mine_user_head);
        mMineUserName = find(R.id.mine_user_name);
        mExit = find(R.id.mine_exit_login);
        mExit.setOnClickListener(this);
        mMineUserName.setText(R.string.now_login);
        VectorDrawableCompat drawable = VectorDrawableCompat.create(getResources(), R.drawable.vd_head, null);
        mMineUserHead.setImageDrawable(drawable);
        mUpdateDot = find(R.id.update_dot);
        mUpdateDot.setRadius(UIUtils.dip2Px(5));
        mUpdateDot.setColor(getResources().getColor(R.color.update_dot));
        if (PublicSPUtil.getInstance().getInt("check_version_code", 0) > BuildConfig.VERSION_CODE)
            mUpdateDot.setVisibility(View.VISIBLE);
        mHandler.postDelayed(LOAD_DATA, 500);
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

    public void initUserInfo() {
        if (NetConfig.isAvailable()) {
            LoadImageHeapler headLoader = new LoadImageHeapler(getActivity(), CROP_HEAD);
            headLoader.loadUserHead(new LoadImageHeapler.ImageLoadListener() {
                @Override
                public void loadImage(Bitmap bmp) {
                    mMineUserHead.setImageBitmap(bmp);
                }
            });
        } else {
            if (cropExists(CROP_HEAD)) {
                setImageFromUri(mMineUserHead, Uri.fromFile(new File(FileUtils.getCachePath(getActivity()) + File.separator + CROP_HEAD)));
            }

        }
    }

    private void setImageFromUri(ImageView imageView, Uri uri) {
        if (imageView == null || uri == null) return;
        try {
            String cropName = CROP_HEAD;
            int degree = getImageDegree(FileUtils.getCachePath(getActivity()) + File.separator + cropName);
            imageView.setImageBitmap(FileUtils.rotateBitmapByDegree(loadBitmap(uri), degree));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap loadBitmap(Uri uri) throws FileNotFoundException {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inSampleSize = 2;
        return BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri), null, opt);
    }

    private boolean cropExists(String cropName) {
        File file = new File(FileUtils.getCachePath(getActivity()), cropName);
        return file.exists() && file.length() > 0;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser && mHandler != null) {
            mHandler.removeCallbacks(LOAD_DATA);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_my_card_toright:
                openActivity(MineCardActivity.class);
                break;
            case R.id.mine_layout:  //用户信息页面
                if (MeManager.getIsLogin()) {
                    //已登录
                    openActivityForResult(PersonalInfoActivity.class, REQUST_CODE);

                } else {
                    //未登录：点击修改密码跳入登录页面
                    openActivityForResult(LoginActivity.class, REQUST_CODE);
                }

                break;
            case R.id.mine_harvestaddress_toright:  // 我的收获地址
                if (!MeManager.getIsLogin()) {
                    //未登录：点击修改密码跳入登录页面
                    openActivityForResult(LoginActivity.class, REQUST_CODE);
                    return;
                } else {
                    openActivity(ReceiptAddressActivity.class);
                }
                break;
            case R.id.mine_recommendfriend_toright: // 推荐好友
                //showShareDialog();
                openActivity(ShareActivity.class);
                break;
            case R.id.mine_changepassword_toright:  //修改密码
                if (!MeManager.getIsLogin()) {
                    //未登录：点击修改密码跳入登录页面
                    openActivityForResult(LoginActivity.class, REQUST_CODE);
                } else {
                    //已登录：点击修改密码进入修改密码页面
                    openActivityForResult(ChangePasswordActivity.class, REQUST_CODE);
                }
                break;
            case R.id.mine_changephone_toright:     // 修改手机
                if (!MeManager.getIsLogin()) {
                    //未登录：点击修改密码跳入登录页面
                    openActivityForResult(LoginActivity.class, REQUST_CODE);
                    return;
                } else {
                    openActivity(ChangePhoneActivity.class);
                }
                break;
            case R.id.mine_aboutus_toright:         //关于我们
                openActivity(AboutUsActivity.class);
                break;
            case R.id.mine_exit_login:
                showExitDialog();
                break;
        }

    }

    private void requestLoginOut() {
        showProgressDialog(R.string.loading);
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("uid", MeManager.getUid());
                jsonObject.put("token", MeManager.getToken());
                Log.e(TAG, String.valueOf(jsonObject));
                e.onNext(OkClient.get(NetConfig.consistUrl(LOGIN_OUT), jsonObject));
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                JSONObject result = new JSONObject(s);
                Log.e(TAG, result.toString());
                String code = result.getString("code");
                if (code.equals("s_ok")) {
                    ToastUtils.showToast(R.string.exitlogin);
                    MeManager.clearAll();
                    MeManager.setIsLgon(false);
                    closeProgressDialog();
                    mHandler.postDelayed(LOAD_DATA, 400);
                } else {
                    closeProgressDialog();
                    ToastUtils.showToast(R.string.request_failed);
                }

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {
                closeProgressDialog();
                ToastUtils.showToast(R.string.request_failed);

            }
        });
    }

    private void showExitDialog() {
        if (!MeManager.getIsLogin()) {
            return;
        }
        View longinDialogView = LayoutInflater.from(getActivity()).inflate(R.layout.exit_login, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        TextView dialogDismiss = (TextView) longinDialogView.findViewById(R.id.dialog_dismiss);
        TextView dialogExit = (TextView) longinDialogView.findViewById(R.id.dialog_exit);
        builder.setView(longinDialogView);
        builder.setCancelable(false);
        final Dialog dialog = builder.show();
        dialogDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialogExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                requestLoginOut();
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUST_CODE:
                if (MeManager.getIsLogin()) {
                    mHandler.postDelayed(LOAD_DATA, 300);
                } else {
                    VectorDrawableCompat drawable = VectorDrawableCompat.create(getResources(), R.drawable.vd_head, null);
                    mMineUserHead.setImageDrawable(drawable);
                    mMineUserName.setText(R.string.now_login);
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        MobclickAgent.onPageStart("FragmentExpand");
        super.onResume();
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("FragmentExpand");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(LOAD_DATA);

    }


}
