package com.etcxc.android.ui.fragment;

import android.content.DialogInterface;
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
import android.widget.TextView;

import com.etcxc.MeManager;
import com.etcxc.android.R;
import com.etcxc.android.base.BaseFragment;
import com.etcxc.android.ui.activity.AboutUsActivity;
import com.etcxc.android.ui.activity.LargeImageActivity;
import com.etcxc.android.ui.activity.MainActivity;
import com.etcxc.android.ui.activity.PersonalInfoAvtivity;
import com.etcxc.android.utils.FileUtils;
import com.etcxc.android.utils.PrefUtils;
import com.etcxc.android.utils.ToastUtils;

import java.io.File;
import java.io.IOException;

import static com.etcxc.android.base.App.isLogin;
import static com.etcxc.android.utils.FileUtils.getCachePath;
/**
 * Created by 刘涛 on 2017/6/2 0002.
 */
public class FragmentMine extends BaseFragment implements View.OnClickListener{
    private File mFile;
    private ImageView mUserHead;
    private TextView mUsername;
    private FrameLayout mMinewLauout;
    //TODO: 2017/7/3
    private Handler mHandler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = (MainActivity) getActivity();
        return inflater.inflate(R.layout.fargment_mine, null);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }
    private void initView() {
        ImageView harvestAddress =find(R.id.mine_harvestaddress_toright);
        ImageView recommendFriend =find(R.id.mine_recommendfriend_toright);
        ImageView changePassWord =find(R.id.mine_changepassword_toright);
        ImageView changePhone =find(R.id.mine_changephone_toright);
        ImageView netWorkTelePhone =find(R.id.mine_networktelephone_toright);
        ImageView aboutUs =find(R.id.mine_aboutus_toright);
        mMinewLauout = find(R.id.mine_layout);
        mUserHead = find(R.id.userhead);
        mUsername = find(R.id.username);
        harvestAddress.setOnClickListener(this);
        recommendFriend.setOnClickListener(this);
        changePassWord.setOnClickListener(this);
        changePhone.setOnClickListener(this);
        netWorkTelePhone.setOnClickListener(this);
        aboutUs.setOnClickListener(this);
        mUserHead.setOnClickListener(this);
        mMinewLauout.setOnClickListener(this);
        mHandler.postDelayed(LOAD_DATA, 500);
    }
    /**
     * 启动延时加载
     */
    private Runnable LOAD_DATA = new Runnable() {
        @Override
        public void run() {
            //从本地加载个人信
            if (MeManager.getIsLogin()) {
                mUsername.setText(MeManager.getSid());
                initData();
            } else {
                VectorDrawableCompat drawable = VectorDrawableCompat.create(getResources(), R.drawable.vd_head2, null);
                mUserHead.setImageDrawable(drawable);
                mUsername.setText("立即登录");
            }

        }
    };

    /**
     * 初始数据
     */
    public void initData() {
        mFile = new File(getCachePath(mActivity), "user-avatar.jpg");
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
        if (!isVisibleToUser) {
            mHandler.removeCallbacks(LOAD_DATA);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_layout:  //用户信息页面
                Intent intent1 = new Intent(mActivity, PersonalInfoAvtivity.class);
                startActivityForResult(intent1, 0);
                break;
            case R.id.userhead: //头像
                if (!isLogin) {
                    ToastUtils.showToast(R.string.nologin);
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
                break;
            case R.id.mine_harvestaddress_toright:  // 我的收获地址
                break;
            case R.id.mine_recommendfriend_toright: // 推荐好友

                showShareDialog();
                break;
            case R.id.mine_changepassword_toright:  //修改密码
                break;
            case R.id.mine_changephone_toright:     // 修改手机
                break;
            case R.id.mine_networktelephone_toright://网点查询
                break;
            case R.id.mine_aboutus_toright:         //关于我们
                startActivity(new Intent(mActivity, AboutUsActivity.class));
                break;
        }

    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param uri
     */
    private Bitmap userBitmap;

    private void getImageToView() {
        //加载本地图片
        final File cover = FileUtils.getSmallBitmap(mActivity, mFile.getPath());
        Uri uri = Uri.fromFile(cover);
        try {
            userBitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), uri);
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
    /**
     * 弹出分享列表
     */
    private void showShareDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(this.getString(R.string.select_share_type));
        builder.setItems(new String[]{this.getString(R.string.smsshare), this.getString(R.string.moreshare)}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                switch (which) {
                    case 0:
                        String url = "http://www.xckjetc.com/";
                        String smsBody = "我正在浏览这个,觉得真不错,推荐给你哦~ 地址:" + url;
                        sendSMS(url,smsBody);
                        break;
                    case 1:
                        String  content =getString(R.string.sharecontent) + "http://www.xckjetc.com/";
                        String imagePath = FileUtils.getCachePath(mActivity) + File.separator + "user-avatar.jpg";
                        shareMore(imagePath,content);
                        break;
                    default:
                        break;
                }
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void shareMore(String str,String content) {
        //由文件得到uri
        Uri imageUri = Uri.fromFile(new File(str));
        Intent intent = new Intent(Intent.ACTION_SEND);
        if(imageUri !=null){
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
        intent.setType("image/*");
        }else{
            intent.setType("text/plain");
        }
        intent.putExtra(Intent.EXTRA_TEXT,content);
        startActivity(Intent.createChooser(intent, getString(R.string.share)));
    }

    /**
     * 发短信
     */
    private void sendSMS(String webUrl,String smsBody) {

        Uri smsToUri = Uri.parse("smsto:");
        Intent sendIntent = new Intent(Intent.ACTION_VIEW, smsToUri);
        //sendIntent.putExtra("address", "123456"); // 电话号码，这行去掉的话，默认就没有电话
        //短信内容
        sendIntent.putExtra("sms_body", smsBody);
        sendIntent.setType("vnd.android-dir/mms-sms");
        startActivityForResult(sendIntent, 1002);
    }
}
