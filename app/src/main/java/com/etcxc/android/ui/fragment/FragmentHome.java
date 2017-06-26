package com.etcxc.android.ui.fragment;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.etcxc.android.BuildConfig;
import com.etcxc.android.R;
import com.etcxc.android.base.App;
import com.etcxc.android.base.BaseFragment;
import com.etcxc.android.ui.activity.ETCIssueActivity;
import com.etcxc.android.ui.activity.MainActivity;
import com.etcxc.android.ui.view.FocusTextview;
import com.etcxc.android.utils.PermissionUtil;
import com.etcxc.android.utils.RxUtil;
import com.etcxc.android.utils.SystemUtil;
import com.etcxc.android.utils.ToastUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by 刘涛 on 2017/6/2 0002.
 */

public class FragmentHome extends BaseFragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    private Handler mHandler = new Handler();
    private GridView mHomeGV;
    private List<ImageView> imageViewList; // Viewpager的数据
    private ViewPager mVPger;
    private TextView mETCRecharge, mETCSave, mETCDetile;
    private View mETCOnline;
    private FocusTextview ft;
    private LinearLayout llPointGroup;
    private int previousPosition = 0; // 前一个被选中的position
    private  static int[] image = {R.drawable.vd_brief_description_of_business,
            R.drawable.vd_recharge_record,
            R.drawable.vd_through_the_detail,
            R.drawable.vd_activate,
            R.drawable.vd_complaint_and_advice,
            R.drawable.vd_gridchek,};
    private  String[] title = {App.get().getString(R.string.bussiness),App.get().getString(R.string.rechargerecord)
            ,App.get().getString(R.string.pass_detail), App.get().getString(R.string.activate),App.get().getString(R.string.advice),
            App.get().getString(R.string.gridchek)};
    private String strDitle ="高速公路畅通无阻\n“0”元照进不误";
    private MainActivity mActivity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = (MainActivity) getActivity();
        View view = inflater.inflate(R.layout.fragment_home, null);
        ft = (FocusTextview) view.findViewById(R.id.ft_tv);
        ft.setEllipsize(android.text.TextUtils.TruncateAt.MARQUEE);
        mVPger = (ViewPager) view.findViewById(R.id.viewpager);
        llPointGroup = (LinearLayout) view.findViewById(R.id.ll_point_group);
        mETCOnline = view.findViewById(R.id.home_etcmore_llayout);//ETC在线办理
        mETCRecharge = (TextView) view.findViewById(R.id.home_etcrecharge_tv);//ETC充值
        mETCSave = (TextView) view.findViewById(R.id.home_etccirclesave_tv);//ETC圈存
        mETCDetile= (TextView) view.findViewById(R.id.home_detile_tv);//0元照进不误
        mHomeGV = (GridView) view.findViewById(R.id.home_gridview);
        initView();
        slideShow();
        return view;
    }
    private void initView() {
        mETCOnline.setOnClickListener(this);
        SpannableStringBuilder style=new SpannableStringBuilder(strDitle);
        //SpannableStringBuilder
        style.setSpan(new TextAppearanceSpan(mActivity, R.style.style0), 0, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(new TextAppearanceSpan(mActivity, R.style.style1), 10, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(new TextAppearanceSpan(mActivity, R.style.style0), 11, 17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mETCDetile.setText(style);//将其添加到tv中
        mHomeGV.setAdapter(new MyGridViewAdapter());
        mHomeGV.setOnItemClickListener(this);
        initData();
        MyAdapter mAdapter = new MyAdapter();
        mVPger.setAdapter(mAdapter); // 当执行完此行代码, ViewPager会去找MyAdapter去要数据.
        // 设置默认选中的点
        previousPosition = 0;
        llPointGroup.getChildAt(previousPosition).setEnabled(true);
        mVPger.setCurrentItem(previousPosition);
        viewPagerListener();
    }
    //启动轮播
    private void slideShow() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                int current = mVPger.getCurrentItem();
                if (current < imageViewList.size() - 1) {
                    current++;
                } else {
                    current = 0;
                }
                mVPger.setCurrentItem(current);
                mHandler.sendEmptyMessageDelayed(0, 3000);
            }
        };
        //发送延时消息,启动自动轮播
        mHandler.sendEmptyMessageDelayed(0, 3000);
    }

    private void viewPagerListener() {
        mVPger.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int newPosition = position % imageViewList.size();
                // 把当前选中的点给切换了, 还有描述信息也切换
                llPointGroup.getChildAt(previousPosition).setEnabled(false);
                llPointGroup.getChildAt(newPosition).setEnabled(true);
                // 把当前的索引赋值给前一个索引变量, 方便下一次再切换.
                previousPosition = newPosition;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mVPger.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        System.out.println("按下");
                        mHandler.removeCallbacksAndMessages(null);//移除消息,停止轮播
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        //事件取消: 当按住头条新闻后,突然上下滑动ListView,导致当前ViewPager事件被取消,而不响应抬起事件
                        System.out.println("取消");
                        //发送延时消息,启动自动轮播
                        mHandler.sendEmptyMessageDelayed(0, 3000);
                        break;
                    case MotionEvent.ACTION_UP:
                        System.out.println("抬起");
                        //发送延时消息,启动自动轮播
                        mHandler.sendEmptyMessageDelayed(0, 3000);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    private void initData() {
        int[] imageResIDs = {R.mipmap.advinfo, R.mipmap.advinfo_one, R.mipmap.advinfo_two,};
        imageViewList = new ArrayList<>();
        ImageView iv;
        View v;
        LayoutParams params;
        for (int i = 0; i < imageResIDs.length; i++) {
            iv = new ImageView(mActivity);
            iv.setBackgroundResource(imageResIDs[i]);
            imageViewList.add(iv);
            // 每循环一次需要向LinearLayout中添加一个点的view对象
            v = new View(mActivity);
            v.setBackgroundResource(R.drawable.point_bg);
            params = new LayoutParams(20, 20);
            if (i != 0) {
                // 当前不是第一个点, 需要设置左边距
                params.leftMargin = 20;
            }
            v.setLayoutParams(params);
            v.setEnabled(false);
            llPointGroup.addView(v);
        }
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

    class MyAdapter extends PagerAdapter {
        //返回的int的值, 会作为ViewPager的总长度来使用.
        @Override
        public int getCount() {
            return imageViewList.size();
        }

        //判断是否使用缓存, 如果返回的是true, 使用缓存. 不去调用instantiateItem方法创建一个新的对象
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // 把position对应位置的ImageView添加到ViewPager中
            ImageView iv = imageViewList.get(position);
           container.addView(iv);
            return iv;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
           container.removeView((View) object);
        }
    }
    private class MyGridViewAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return image.length;
        }
        @Override
        public Object getItem(int item) {
            return null;
        }
        @Override
        public long getItemId(int id) {
            return 0;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View viewGV = View.inflate(mActivity, R.layout.item_home_gridview, null);
            ImageView iv_home_gn = (ImageView) viewGV.findViewById(R.id.item_home_gv_iv);
            TextView tv_item_title = (TextView) viewGV.findViewById(R.id.item_home_gv_tv);
            iv_home_gn.setImageResource(image[position]);
            tv_item_title.setText(title[position]);
            return viewGV;
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
        mHandler.removeCallbacksAndMessages(null);//移除消息,停止轮播
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
