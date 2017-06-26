package com.etcxc.android.ui.fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.etcxc.MeManager;
import com.etcxc.android.R;
import com.etcxc.android.base.App;
import com.etcxc.android.base.BaseFragment;
import com.etcxc.android.ui.activity.LargeImageActivity;
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

public class FragmentMine extends BaseFragment implements View.OnClickListener {
    private File file;
    private Uri uri;
    private ImageView userHead;
    private TextView username;
    private FrameLayout mMinewLauout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fargment_mine, null);
        mMinewLauout    = (FrameLayout) view.findViewById(R.id.mine_layout);
        userHead = (ImageView) view.findViewById(R.id.userhead);
        username = (TextView) view.findViewById(R.id.username);

        initView();
        return view;
    }
    private void initView() {
        //从本地加载个人信
        isLogin = MeManager.getIsLogin();
        if(isLogin){
            String name = MeManager.getSid();
            username.setText(name);
        }
        userHead.setOnClickListener(this);
        mMinewLauout.setOnClickListener(this);

        setStartHead();
    }


    public void setStartHead() {
        file = new File(getCachePath(getActivity()), "user-avatar.jpg");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            uri = Uri.fromFile(file);
        } else {
            uri = FileProvider.getUriForFile(App.get(), "com.etcxc.useravatar", file);
        }
        if(file.exists()) {
            getImageToView();//初始化
        }else{
            VectorDrawableCompat drawable = VectorDrawableCompat.create(getResources(),  R.drawable.vd_head2, null);
            userHead.setImageDrawable(drawable);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_layout:  //用户信息页面
                Intent intent1 = new Intent(getActivity(), PersonalInfoAvtivity.class);
                startActivity(intent1);
                break;
            case R.id.userhead: //头像
               String path = FileUtils.getCachePath(getActivity())+File.separator+"user-avatar.jpg";
                File file = new File(path);
                if (!file.exists()){
                    ToastUtils.showToast(R.string.nosethead);
                    return;
                }
                PrefUtils.setBoolean(getActivity(),"isScal",true);
                //加载本地图片
                Intent intent4 = new Intent(getActivity(), LargeImageActivity.class);
                intent4.putExtra("path", FileUtils.getCachePath(getActivity())+File.separator+"user-avatar.jpg");
                startActivity(intent4);
                getActivity().overridePendingTransition(R.anim.zoom_enter,R.anim.anim_out);
                break;
            case R.id.username:
                break;
        }

    }
    public void setName(String name){
        username.setText(name);
    }
    /**
     * 保存裁剪之后的图片数据
     * @param uri
     */
    private Bitmap userBitmap;
    private void getImageToView() {
        //加载本地图片
        final File cover = FileUtils.getSmallBitmap(getActivity(), file.getPath());
        Uri uri = Uri.fromFile(cover);
        try {
            userBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(userBitmap!= null){
            userHead.setImageBitmap(FileUtils.toRoundBitmap(userBitmap));
        }
    }
    @Override
    public void onResume() {
        setStartHead();
        super.onResume();
    }

}
