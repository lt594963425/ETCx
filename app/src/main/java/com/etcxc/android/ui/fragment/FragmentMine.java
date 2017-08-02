package com.etcxc.android.ui.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.etcxc.MeManager;
import com.etcxc.android.BuildConfig;
import com.etcxc.android.R;
import com.etcxc.android.base.BaseFragment;
import com.etcxc.android.modle.sp.PublicSPUtil;
import com.etcxc.android.ui.activity.AboutUsActivity;
import com.etcxc.android.ui.activity.ChangePasswordActivity;
import com.etcxc.android.ui.activity.ChangePhoneActivity;
import com.etcxc.android.ui.activity.LargeImageActivity;
import com.etcxc.android.ui.activity.LoginActivity;
import com.etcxc.android.ui.activity.PersonalInfoActivity;
import com.etcxc.android.ui.activity.ReceiptAddressActivity;
import com.etcxc.android.ui.activity.ShareActivity;
import com.etcxc.android.ui.view.ColorCircle;
import com.etcxc.android.utils.FileUtils;
import com.etcxc.android.utils.PrefUtils;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.UIUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.IOException;

import static com.etcxc.MeManager.setIsLgon;
import static com.etcxc.android.utils.FileUtils.getCachePath;

/**
 * Created by 刘涛 on 2017/6/2 0002.
 */
public class FragmentMine extends BaseFragment implements View.OnClickListener {
    private RelativeLayout  mHarvestAddress, mRecommendFriend, mChangePassWord, mChangePhone, mAboutUs;
    private File mFile;
    private ImageView mUserHead;
    private TextView mUsername,mExit;
    private FrameLayout mMinewLauout;
    private Handler mHandler = new Handler();
    private ColorCircle mUpdateDot;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fargment_mine, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        mHarvestAddress = find(R.id.mine_harvestaddress_toright);
        mRecommendFriend = find(R.id.mine_recommendfriend_toright);
        mChangePassWord = find(R.id.mine_changepassword_toright);
        mChangePhone = find(R.id.mine_changephone_toright);
        mAboutUs = find(R.id.mine_aboutus_toright);
        mMinewLauout = find(R.id.mine_layout);
        mUserHead = find(R.id.userhead);
        mUsername = find(R.id.username);
        mExit =find(R.id.mine_exit_login);
        mHarvestAddress.setOnClickListener(this);
        mRecommendFriend.setOnClickListener(this);
        mChangePassWord.setOnClickListener(this);
        mChangePhone.setOnClickListener(this);
        mAboutUs.setOnClickListener(this);
        mUserHead.setOnClickListener(this);
        mMinewLauout.setOnClickListener(this);
        mExit.setOnClickListener(this);
        mUpdateDot = find(R.id.update_dot);
        mUpdateDot.setRadius(UIUtils.dip2Px(5));
        mUpdateDot.setColor(getResources().getColor(R.color.update_dot));
        if (PublicSPUtil.getInstance().getInt("check_version_code", 0) > BuildConfig.VERSION_CODE) mUpdateDot.setVisibility(View.VISIBLE);
        mHandler.postDelayed(LOAD_DATA, 500);
    }

    /**
     * 启动延时加载
     */
    private Runnable LOAD_DATA = new Runnable() {
        @Override
        public void run() {
            if (MeManager.getIsLogin()) {
                mUsername.setText(MeManager.getUid());
                initData();
            } else {
                VectorDrawableCompat drawable = VectorDrawableCompat.create(getResources(), R.drawable.vd_head2, null);
                mUserHead.setImageDrawable(drawable);
                mUsername.setText(R.string.now_login);
            }
        }
    };

    public void initData() {
        mFile = new File(getCachePath( getActivity()), "user-avatar.jpg");
        if (mFile.exists()) {
            getImageToView();//初始化
        } else {
            VectorDrawableCompat drawable = VectorDrawableCompat.create(getResources(), R.drawable.vd_head2, null);
            mUserHead.setImageDrawable(drawable);
        }
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
            case R.id.mine_layout:  //用户信息页面
                if (!MeManager.getIsLogin()) {
                    //未登录：点击修改密码跳入登录页面
                    openActivity(LoginActivity.class);
                } else {
                    //已登录
                    openActivity(PersonalInfoActivity.class);
                }
                break;
            case R.id.userhead: //头像
                startLoadHead();
                break;
            case R.id.mine_harvestaddress_toright:  // 我的收获地址
                if (!MeManager.getIsLogin()) {
                    ToastUtils.showToast(R.string.nologin);
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
                    openActivityForResult(PersonalInfoActivity.class,0);
                } else {
                    //已登录：点击修改密码进入修改密码页面
                    openActivity(ChangePasswordActivity.class);
                }
                break;
            case R.id.mine_changephone_toright:     // 修改手机
                if (!MeManager.getIsLogin()) {
                    //未登录：点击修改密码跳入登录页面
                    openActivityForResult(LoginActivity.class,0);
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

    private void exitLogin() {
        MeManager.logoutClear();
        MeManager.loginClear();
        setIsLgon(false);
        mHandler.postDelayed(LOAD_DATA, 200);
        ToastUtils.showToast(R.string.exitlogin);
    }
    private void showExitDialog() {
        if (!MeManager.getIsLogin()) {
          mExit.setVisibility(View.INVISIBLE);
        }else{
            mExit.setVisibility(View.VISIBLE);
        }
        View longinDialogView = LayoutInflater.from(getActivity()).inflate(R.layout.exit_login, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        TextView dialogDismiss = (TextView) longinDialogView.findViewById(R.id.dialog_dismiss);
        TextView dialogExit = (TextView) longinDialogView.findViewById(R.id.dialog_exit);
        builder.setView(longinDialogView);
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
                exitLogin();
                dialog.dismiss();
            }
        });
    }
    private void startLoadHead() {
        if (!MeManager.getIsLogin()) {
            ToastUtils.showToast(R.string.nologin);
            openActivityForResult(LoginActivity.class,2);
            return;
        }
        String path = FileUtils.getCachePath(getActivity()) + File.separator + "user-avatar.jpg";
        File file = new File(path);
        if (!file.exists()) {
            ToastUtils.showToast(R.string.nosethead);
            return;
        }
        //PublicSPUtil.getInstance().putBoolean("isScal",true);
        PrefUtils.setBoolean(getActivity(), "isScal", true);
        //加载本地图片
        Intent intent4 = new Intent(getActivity(), LargeImageActivity.class);
        intent4.putExtra("path", FileUtils.getCachePath(getActivity()) + File.separator + "user-avatar.jpg");
        startActivity(intent4);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param uri
     */
    private Bitmap userBitmap;

    private void getImageToView() {
        //加载本地图片
        final File cover = FileUtils.getSmallBitmap(mFile.getPath());
        Uri uri = Uri.fromFile(cover);
        try {
            userBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (userBitmap != null) {
            mUserHead.setImageBitmap(FileUtils.toRoundBitmap(userBitmap));
        }
    }

    /**
     * 回调
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                mHandler.postDelayed(LOAD_DATA, 200);
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
    }
}
