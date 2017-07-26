package com.etcxc.android.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.etcxc.android.R;
import com.etcxc.android.base.App;
import com.etcxc.android.base.BaseFragment;
import com.etcxc.android.ui.activity.ETCIssueActivity;
import com.etcxc.android.ui.activity.ETCRechargeActivity;
import com.etcxc.android.ui.activity.MainActivity;
import com.etcxc.android.ui.activity.StoreActivity;
import com.etcxc.android.ui.adapter.GlideImageLoader;
import com.etcxc.android.ui.adapter.MyGridViewAdapter;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.UIUtils;
import com.umeng.analytics.MobclickAgent;
import com.youth.banner.Banner;
import com.youth.banner.Transformer;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by 刘涛 on 2017/6/2 0002.
 */

public class FragmentHome extends BaseFragment implements AdapterView.OnItemClickListener, View.OnClickListener {
   private final static String TAG = "FragmentHome";
    String[] imagess = new String[]{
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1498558224830&di=b546d2811f9fa910decc55b981f8df8c&imgtype=0&src=http%3A%2F%2Fpic2.ooopic.com%2F11%2F77%2F47%2F63bOOOPIC74_1024.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1498558224830&di=b546d2811f9fa910decc55b981f8df8c&imgtype=0&src=http%3A%2F%2Fpic2.ooopic.com%2F11%2F77%2F47%2F63bOOOPIC74_1024.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1498558224830&di=b546d2811f9fa910decc55b981f8df8c&imgtype=0&src=http%3A%2F%2Fpic2.ooopic.com%2F11%2F77%2F47%2F63bOOOPIC74_1024.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1498558224830&di=b546d2811f9fa910decc55b981f8df8c&imgtype=0&src=http%3A%2F%2Fpic2.ooopic.com%2F11%2F77%2F47%2F63bOOOPIC74_1024.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1498558224830&di=b546d2811f9fa910decc55b981f8df8c&imgtype=0&src=http%3A%2F%2Fpic2.ooopic.com%2F11%2F77%2F47%2F63bOOOPIC74_1024.jpg"};
    private GridView mHomeGV;
    private ViewPager mVPger;
    private LinearLayout mETCOnline;
    private RelativeLayout mETCRecharge,mETCSave;
    private static int[] image = {
            R.drawable.vd_brief_description_of_business,
            R.drawable.vd_recharge_record,
            R.drawable.vd_through_the_detail,
            R.drawable.vd_activate,
            R.drawable.vd_complaint_and_advice,
            R.drawable.vd_gridchek,};
    private String[] title = {
            App.get().getString(R.string.bussiness), App.get().getString(R.string.rechargerecord)
            , App.get().getString(R.string.pass_detail), App.get().getString(R.string.activate), App.get().getString(R.string.advice),
            App.get().getString(R.string.gridchek)};
    private MainActivity mActivity;
    private Banner banner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = (MainActivity) getActivity();
        View view = inflater.inflate(R.layout.fragment_home, null);
        mETCOnline = (LinearLayout) view.findViewById(R.id.home_etc_online_lly);//ETC在线办理
        mETCRecharge = (RelativeLayout) view.findViewById(R.id.home_etc_recharge_rly);//ETC充值
        mETCSave = (RelativeLayout) view.findViewById(R.id.home_etc_circle_save_rly);//ETC圈存
        mHomeGV = (GridView) view.findViewById(R.id.home_gridview);
        //轮播图
        banner = (Banner) view.findViewById(R.id.home_banner);
        banner.setImages(new ArrayList<>(Arrays.asList(imagess))).setImageLoader(new GlideImageLoader()).start();
        banner.setBannerAnimation(Transformer.Accordion);
        initView();
        return view;
    }

    private void initView() {
        mETCOnline.setOnClickListener(this);
        mETCRecharge.setOnClickListener(this);
        mETCSave.setOnClickListener(this);
        mHomeGV.setAdapter(new MyGridViewAdapter(image, title, getActivity()));
        mHomeGV.setOnItemClickListener(this);

    }

    /**
     * GridView
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:   //业务办理
                Log.e(TAG, "" + UIUtils.getDeviceInfo(mActivity));
                ToastUtils.showToast("" + position);
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
            case R.id.home_etc_online_lly:
                startActivity(new Intent(mActivity, ETCIssueActivity.class));
                MobclickAgent.onEvent(mActivity, "mETCIssueClick");
                break;
            case R.id.home_etc_recharge_rly:
                startActivity(new Intent(mActivity, ETCRechargeActivity.class));
                MobclickAgent.onEvent(mActivity, "mETCRechargeClick");
                break;
            case R.id.home_etc_circle_save_rly:
                startActivity(new Intent(mActivity, StoreActivity.class));
                break;
        }

    }

    @Override
    public void onStart() {
        banner.startAutoPlay();
        super.onStart();
    }

    @Override
    public void onStop() {
        banner.stopAutoPlay();
        super.onStop();
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
