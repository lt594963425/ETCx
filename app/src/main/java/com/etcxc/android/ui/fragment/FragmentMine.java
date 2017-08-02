package com.etcxc.android.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
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
import com.etcxc.android.ui.activity.MainActivity;
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

import static com.etcxc.android.utils.FileUtils.getCachePath;

/**
 * Created by 刘涛 on 2017/6/2 0002.
 */
public class FragmentMine extends BaseFragment implements View.OnClickListener {
    private RelativeLayout  mHarvestAddress, mRecommendFriend, mChangePassWord, mChangePhone, mAboutUs;
    private File mFile;
    private ImageView mUserHead;
    private TextView mUsername;
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
        mHarvestAddress.setOnClickListener(this);
        mRecommendFriend.setOnClickListener(this);
        mChangePassWord.setOnClickListener(this);
        mChangePhone.setOnClickListener(this);
        mAboutUs.setOnClickListener(this);
        mUserHead.setOnClickListener(this);
        mMinewLauout.setOnClickListener(this);
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
        mFile = new File(getCachePath((MainActivity) getActivity()), "user-avatar.jpg");
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
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, 0);
                } else {
                    //已登录
                    Intent intent1 = new Intent(getActivity(), PersonalInfoActivity.class);
                    startActivity(intent1);
                }

                break;
            case R.id.userhead: //头像
                startLoadHead();
                break;
            case R.id.mine_harvestaddress_toright:  // 我的收获地址
                if (!MeManager.getIsLogin()) {
                    //未登录：点击修改密码跳入登录页面
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent,0);
                    return;
                } else {
                    startActivity(new Intent(getActivity(), ReceiptAddressActivity.class));
                }
                break;
            case R.id.mine_recommendfriend_toright: // 推荐好友
                //showShareDialog();
                startActivity(new Intent(getActivity(), ShareActivity.class));
                break;
            case R.id.mine_changepassword_toright:  //修改密码
                if (!MeManager.getIsLogin()) {
                    //未登录：点击修改密码跳入登录页面
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent,0);
                } else {
                    //已登录：点击修改密码进入修改密码页面
                    startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
                }
                break;
            case R.id.mine_changephone_toright:     // 修改手机
                if (!MeManager.getIsLogin()) {
                    //未登录：点击修改密码跳入登录页面
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent,0);
                    return;
                } else {
                    startActivity(new Intent(getActivity(), ChangePhoneActivity.class));
                }
                break;
            case R.id.mine_aboutus_toright:         //关于我们
                startActivity(new Intent(getActivity(), AboutUsActivity.class));
                break;
        }

    }
    private void startLoadHead() {
        if (!MeManager.getIsLogin()) {
            ToastUtils.showToast(R.string.nologin);
            Intent intent2 = new Intent(mActivity, LoginActivity.class);
            startActivityForResult(intent2, 0);
            return;
        }
        String path = FileUtils.getCachePath(mActivity) + File.separator + "user-avatar.jpg";
        File file = new File(path);
        if (!file.exists()) {
            ToastUtils.showToast(R.string.nosethead);
            return;
        }
        PrefUtils.setBoolean(mActivity, "isScal", true);
        //加载本地图片
        Intent intent4 = new Intent(mActivity, LargeImageActivity.class);
        intent4.putExtra("path", FileUtils.getCachePath(mActivity) + File.separator + "user-avatar.jpg");
        startActivity(intent4);
        mActivity.overridePendingTransition(R.anim.zoom_enter, R.anim.anim_out);
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
