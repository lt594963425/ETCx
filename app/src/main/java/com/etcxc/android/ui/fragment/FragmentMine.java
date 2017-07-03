package com.etcxc.android.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.etcxc.MeManager;
import com.etcxc.android.R;
import com.etcxc.android.base.App;
import com.etcxc.android.base.BaseFragment;
import com.etcxc.android.ui.activity.AboutUsActivity;
import com.etcxc.android.ui.activity.LargeImageActivity;
import com.etcxc.android.ui.activity.MainActivity;
import com.etcxc.android.ui.activity.PersonalInfoAvtivity;
import com.etcxc.android.utils.FileUtils;
import com.etcxc.android.utils.PrefUtils;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.UIUtils;

import java.io.File;
import java.io.IOException;

import static com.etcxc.android.base.App.isLogin;
import static com.etcxc.android.utils.FileUtils.getCachePath;


/**
 * Created by 刘涛 on 2017/6/2 0002.
 */

public class FragmentMine extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private static int[] image = {
            R.drawable.vd_mine_harvestaddress,
            R.drawable.vd_mine_recommendfriend,
            R.drawable.vd_mine_changepassword,
            R.drawable.vd_mine_changephone,
            R.drawable.vd_mine_networktelephone,
            R.drawable.vd_mine_aboutus};
    private String[] title = {
            App.get().getString(R.string.harvestaddress), App.get().getString(R.string.recommendfriend)
            , App.get().getString(R.string.changepassword), App.get().getString(R.string.changephone),
            App.get().getString(R.string.networktelephone), App.get().getString(R.string.about_us)};
    private File file;
    private Uri uri;
    private ImageView userHead;
    private TextView username;
    private FrameLayout mMinewLauout;
    private Handler mHandler = new Handler();
    private MainActivity mActivity;
    private ListView mlistView;

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
        mlistView = find(R.id.mine_listview);
        mlistView.setDividerHeight(UIUtils.px2Dip(10));
        mMinewLauout = find(R.id.mine_layout);
        userHead = find(R.id.userhead);
        username = find(R.id.username);
        userHead.setOnClickListener(this);
        mMinewLauout.setOnClickListener(this);
        mHandler.postDelayed(LOAD_DATA, 500);
        mlistView.setAdapter(new MineListViewAdapter(getActivity(), image, title));
        mlistView.setOnItemClickListener(this);

    }

    /**
     * 启动延时加载
     */
    private Runnable LOAD_DATA = new Runnable() {
        @Override
        public void run() {
            //从本地加载个人信
            if (MeManager.getIsLogin()) {
                username.setText(MeManager.getSid());
                initData();
            } else {
                VectorDrawableCompat drawable = VectorDrawableCompat.create(getResources(), R.drawable.vd_head2, null);
                userHead.setImageDrawable(drawable);
                username.setText("立即登录");
            }

        }
    };

    /**
     * 初始数据
     */
    public void initData() {
        file = new File(getCachePath(mActivity), "user-avatar.jpg");

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            uri = Uri.fromFile(file);
        } else {
            uri = FileProvider.getUriForFile(App.get(), "com.etcxc.useravatar", file);
        }
        if (file.exists()) {
            getImageToView();//初始化
        } else {
            VectorDrawableCompat drawable = VectorDrawableCompat.create(getResources(), R.drawable.vd_head2, null);
            userHead.setImageDrawable(drawable);
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
            case R.id.username:
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
        final File cover = FileUtils.getSmallBitmap(mActivity, file.getPath());
        Uri uri = Uri.fromFile(cover);
        try {
            userBitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (userBitmap != null) {
            userHead.setImageBitmap(FileUtils.toRoundBitmap(userBitmap));
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:// 我的收获地址
                startActivity(new Intent(mActivity, AboutUsActivity.class));
                break;
            case 1:// 推荐好友
                showShareDialog();
                break;
            case 2://修改密码
                startActivity(new Intent(mActivity, AboutUsActivity.class));
                break;
            case 3:// 修改手机
                startActivity(new Intent(mActivity, AboutUsActivity.class));
                break;
            case 4: //网点查询
                startActivity(new Intent(mActivity, AboutUsActivity.class));
                break;
            case 5: //关于我们

                startActivity(new Intent(mActivity, AboutUsActivity.class));
                break;
        }
    }

    /**
     * 弹出分享列表
     */
    private void showShareDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("选择分享类型");
        builder.setItems(new String[]{this.getString(R.string.smsshare),this.getString(R.string.moreshare)}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                switch (which) {

                    case 0:
                        sendSMS("http://www.xckjetc.com/");
                        break;
                    case 1:
                        Intent intent=new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_SUBJECT,"分享");
                        intent.putExtra(Intent.EXTRA_TEXT, "分享我们的app下载连接连接，后台提供，待添加。。。。。迅畅官网："+"http://www.xckjetc.com/");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(Intent.createChooser(intent, "分享"));
                        break;
                    default:
                        break;
                }

            }
        });
        builder.setNegativeButton( "取消" ,  new  DialogInterface.OnClickListener() {
            @Override
            public   void  onClick(DialogInterface dialog,  int  which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }



    /**
     * 发短信
     */
    private   void  sendSMS(String webUrl){
        String smsBody = "我正在浏览这个,觉得真不错,推荐给你哦~ 地址:" + webUrl;
        Uri smsToUri = Uri.parse( "smsto:" );
        Intent sendIntent =  new  Intent(Intent.ACTION_VIEW, smsToUri);
        //sendIntent.putExtra("address", "123456"); // 电话号码，这行去掉的话，默认就没有电话
        //短信内容
        sendIntent.putExtra( "sms_body", smsBody);
        sendIntent.setType( "vnd.android-dir/mms-sms" );
        startActivityForResult(sendIntent, 1002 );
    }
    class MineListViewAdapter extends BaseAdapter {
        private int [] image ;
        private String [] title ;
        private Context context;
        public MineListViewAdapter( Context context,int[] image, String[] title) {
            this.image = image;
            this.title = title;
            this.context =context;
        }
        @Override
        public int getCount() {
            return title.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vH = null;
            if(convertView ==null) {
                vH = new ViewHolder();
                convertView= LayoutInflater.from(context).inflate(R.layout.item_mine_listview, parent, false);
                vH.image = (ImageView) convertView.findViewById(R.id.mine_lv_iv);
                vH.title = (TextView) convertView.findViewById(R.id.mine_lv_tv);
                convertView.setTag(vH);
            }else {
                vH = (ViewHolder) convertView.getTag();
            }
            vH.image.setImageResource(image[position]);
            vH.title.setText(title[position]);

            return convertView;
        }
        public final class ViewHolder {
            public ImageView image;
            public TextView title;
        }
    }
}
