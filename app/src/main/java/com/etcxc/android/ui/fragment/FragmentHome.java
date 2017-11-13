package com.etcxc.android.ui.fragment;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.etcxc.android.R;
import com.etcxc.android.base.App;
import com.etcxc.android.base.BaseFragment;
import com.etcxc.android.ui.activity.ETCIssueActivity;
import com.etcxc.android.ui.activity.ETCRechargeActivity;
import com.etcxc.android.ui.activity.NetworkQueryActivity;
import com.etcxc.android.ui.activity.StoreActivity;
import com.etcxc.android.ui.activity.UploadLicenseActivity;
import com.etcxc.android.ui.adapter.BaseSelectAdapter;
import com.etcxc.android.ui.adapter.GlideImageLoader;
import com.etcxc.android.ui.adapter.HomeRecyclerAdapter;
import com.etcxc.android.ui.view.GridRecyclerView;
import com.etcxc.android.ui.view.ItemOffsetDecoration;
import com.etcxc.android.utils.LogUtil;
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

public class FragmentHome extends BaseFragment implements View.OnClickListener, BaseSelectAdapter.OnItemClickListener {
    private final static String TAG = "FragmentHome";
    String[] mBannerImagess = new String[]{
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1498558224830&di=b546d2811f9fa910decc55b981f8df8c&imgtype=0&src=http%3A%2F%2Fpic2.ooopic.com%2F11%2F77%2F47%2F63bOOOPIC74_1024.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1498558224830&di=b546d2811f9fa910decc55b981f8df8c&imgtype=0&src=http%3A%2F%2Fpic2.ooopic.com%2F11%2F77%2F47%2F63bOOOPIC74_1024.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1498558224830&di=b546d2811f9fa910decc55b981f8df8c&imgtype=0&src=http%3A%2F%2Fpic2.ooopic.com%2F11%2F77%2F47%2F63bOOOPIC74_1024.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1498558224830&di=b546d2811f9fa910decc55b981f8df8c&imgtype=0&src=http%3A%2F%2Fpic2.ooopic.com%2F11%2F77%2F47%2F63bOOOPIC74_1024.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1498558224830&di=b546d2811f9fa910decc55b981f8df8c&imgtype=0&src=http%3A%2F%2Fpic2.ooopic.com%2F11%2F77%2F47%2F63bOOOPIC74_1024.jpg"};
    private GridRecyclerView mHomeRecycler;
    private static int[] mImage = {
            R.drawable.vd_brief_description_of_business,
            R.drawable.vd_recharge_record,
            R.drawable.vd_gridchek,
            R.drawable.vd_through_the_detail,
            R.drawable.vd_complaint_and_advice,
            R.drawable.vd_activate
    };
    private String[] mTitle = {
            App.get().getString(R.string.bill_check), App.get().getString(R.string.electronic_invoice)
            , App.get().getString(R.string.website_check), App.get().getString(R.string.traffic_status)
            , App.get().getString(R.string.fare_calculate), App.get().getString(R.string.coming_soon)
    };
    private Banner mBanner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        view.findViewById(R.id.home_etc_online_lly).setOnClickListener(this);//ETC在线办理
        view.findViewById(R.id.home_etc_recharge_rly).setOnClickListener(this);//ETC充值
        view.findViewById(R.id.home_etc_circle_save_rly).setOnClickListener(this);//ETC圈存
        mHomeRecycler = view.findViewById(R.id.home_recylerview);
        mBanner = view.findViewById(R.id.home_banner);
        mBanner.setImages(new ArrayList<>(Arrays.asList(mBannerImagess))).setImageLoader(new GlideImageLoader()).start();
        mBanner.setBannerAnimation(Transformer.Accordion);
        setReCyclerView();
        runLayoutAnimation();
    }

    private void setReCyclerView() {
        GridLayoutManager gridLayManager = new GridLayoutManager(getActivity(), 3);
        gridLayManager.setOrientation(GridLayoutManager.VERTICAL);
        mHomeRecycler.setLayoutManager(gridLayManager);
        HomeRecyclerAdapter homeRecyclerAdapter = new HomeRecyclerAdapter(mTitle, mImage);
        homeRecyclerAdapter.setOnItemClickListener(this);
        mHomeRecycler.setAdapter(homeRecyclerAdapter);
        final int spacing = getResources().getDimensionPixelOffset(R.dimen.default_spacing_small);
        mHomeRecycler.addItemDecoration(new ItemOffsetDecoration(spacing));
        mHomeRecycler.setHasFixedSize(true);

    }

    private void runLayoutAnimation() {
        final Context context = mHomeRecycler.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.grid_layout_animation_scale);
        mHomeRecycler.setLayoutAnimation(controller);
        mHomeRecycler.getAdapter().notifyDataSetChanged();
        mHomeRecycler.scheduleLayoutAnimation();
    }

    @Override
    public void onItemClick(View view, int position) {
        switch (position) {
            case 0:
                Log.e(TAG, "" + UIUtils.getDeviceInfo(getActivity()));
                ToastUtils.showToast("" + position);
                break;
            case 1:
                openActivity(UploadLicenseActivity.class);
                break;
            case 2://网点查询
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                openActivity(NetworkQueryActivity.class);
                break;
            case 3:

                break;
            case 4:

                break;
            case 5:
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
        mBanner.startAutoPlay();
        super.onStart();
    }

    @Override
    public void onStop() {
        mBanner.stopAutoPlay();
        super.onStop();
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
    }


}
