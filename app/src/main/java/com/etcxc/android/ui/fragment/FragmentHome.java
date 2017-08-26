package com.etcxc.android.ui.fragment;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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
import com.etcxc.android.ui.activity.IssueFinishActivity;
import com.etcxc.android.ui.activity.IssuePayActivity;
import com.etcxc.android.ui.activity.NetworkQueryActivity;
import com.etcxc.android.ui.activity.StoreActivity;
import com.etcxc.android.ui.adapter.GlideImageLoader;
import com.etcxc.android.ui.adapter.SmallFeatureAdapter;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.UIUtils;
import com.umeng.analytics.MobclickAgent;
import com.youth.banner.Banner;
import com.youth.banner.Transformer;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * 首页
 * Created by LiuTao on 2017/6/2 0002.
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
    private LinearLayout mETCOnline;
    private RelativeLayout mETCRecharge,mETCSave;
    private static int[] image = {
            R.drawable.vd_brief_description_of_business,
            R.drawable.vd_recharge_record,
            R.drawable.vd_gridchek,
            R.drawable.vd_through_the_detail,
            R.drawable.vd_complaint_and_advice,
            R.drawable.vd_activate
         };
    private String[] title = {
            App.get().getString(R.string.bill_check), App.get().getString(R.string.electronic_invoice)
            , App.get().getString(R.string.website_check),App.get().getString(R.string.traffic_status)
            , App.get().getString(R.string.fare_calculate), App.get().getString(R.string.coming_soon)
           };
    private Banner banner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        mHomeGV.setAdapter(new SmallFeatureAdapter(image, title, getActivity()));
        mHomeGV.setOnItemClickListener(this);

    }

    /**
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                Log.e(TAG, "" + UIUtils.getDeviceInfo(getActivity()));
                ToastUtils.showToast("" + position);
                break;
            case 1:
                break;
            case 2://网点查询
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                openActivity(NetworkQueryActivity.class);
                break;
            case 3:
                openActivity(IssueFinishActivity.class);
                break;
            case 4:
                break;
            case 5:
                openActivity(IssuePayActivity.class);
                break;
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_etc_online_lly:
                MobclickAgent.onEvent(getActivity(), "mETCIssueClick");
                openActivity(ETCIssueActivity.class);
                break;
            case R.id.home_etc_recharge_rly:
                MobclickAgent.onEvent(getActivity(), "mETCRechargeClick");
                openActivity(ETCRechargeActivity.class);

                break;
            case R.id.home_etc_circle_save_rly:
                MobclickAgent.onEvent(getActivity(), "StoreClick");
                openActivity(StoreActivity.class);
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
