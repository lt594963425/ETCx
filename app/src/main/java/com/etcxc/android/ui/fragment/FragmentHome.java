package com.etcxc.android.ui.fragment;


import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
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
import com.etcxc.android.ui.activity.IssueFinishActivity;
import com.etcxc.android.ui.activity.IssuePayActivity;
import com.etcxc.android.ui.activity.NetworkQueryActivity;
import com.etcxc.android.ui.activity.StoreActivity;
import com.etcxc.android.ui.activity.UploadLicenseActivity;
import com.etcxc.android.ui.adapter.BaseSelectAdapter;
import com.etcxc.android.ui.adapter.GlideImageLoader;
import com.etcxc.android.ui.adapter.HomeRecyclerAdapter;
import com.etcxc.android.ui.view.GridRecyclerView;
import com.etcxc.android.ui.view.ItemOffsetDecoration;
import com.etcxc.android.utils.LogUtil;
import com.umeng.analytics.MobclickAgent;
import com.youth.banner.Banner;
import com.youth.banner.Transformer;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * 首页
 * Created by LiuTao on 2017/6/2 0002.
 */

public class FragmentHome extends BaseFragment implements View.OnClickListener {
    private final static String TAG = "FragmentHome";
    private static final Class<?>[] ACTIVITY = {null,
            UploadLicenseActivity.class,
            NetworkQueryActivity.class,
            IssueFinishActivity.class,
            IssuePayActivity.class, null};
    String[] mBannerImagess = new String[]{
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1498558224830&di=b546d2811f9fa910decc55b981f8df8c&imgtype=0&src=http%3A%2F%2Fpic2.ooopic.com%2F11%2F77%2F47%2F63bOOOPIC74_1024.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1498558224830&di=b546d2811f9fa910decc55b981f8df8c&imgtype=0&src=http%3A%2F%2Fpic2.ooopic.com%2F11%2F77%2F47%2F63bOOOPIC74_1024.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1498558224830&di=b546d2811f9fa910decc55b981f8df8c&imgtype=0&src=http%3A%2F%2Fpic2.ooopic.com%2F11%2F77%2F47%2F63bOOOPIC74_1024.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1498558224830&di=b546d2811f9fa910decc55b981f8df8c&imgtype=0&src=http%3A%2F%2Fpic2.ooopic.com%2F11%2F77%2F47%2F63bOOOPIC74_1024.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1498558224830&di=b546d2811f9fa910decc55b981f8df8c&imgtype=0&src=http%3A%2F%2Fpic2.ooopic.com%2F11%2F77%2F47%2F63bOOOPIC74_1024.jpg"};
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
    private GridRecyclerView mHomeRecycler;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_home, null);
        initView(view);
        initReCyclerView();
        runLayoutAnimation();
        initData();
        initBanner();
        return view;
    }
    private void initView(View view) {
        mHomeRecycler = (GridRecyclerView) view.findViewById(R.id.home_recylerview);
        mBanner = (Banner) view.findViewById(R.id.home_banner);
        view.findViewById(R.id.home_etc_online_lly).setOnClickListener(this);//ETC在线办理
        view.findViewById(R.id.home_etc_recharge_rly).setOnClickListener(this);//ETC充值
        view.findViewById(R.id.home_etc_circle_save_rly).setOnClickListener(this);//ETC圈存
    }
    private void initData() {
    }
    private void initBanner() {
        mBanner.setImages(new ArrayList<>(Arrays.asList(mBannerImagess))).setImageLoader(new GlideImageLoader()).start();
        mBanner.setBannerAnimation(Transformer.Accordion);
    }
    private void initReCyclerView() {
        mHomeRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        HomeRecyclerAdapter homeRecyclerAdapter = new HomeRecyclerAdapter(mTitle, mImage);
        mHomeRecycler.setAdapter(homeRecyclerAdapter);
        mHomeRecycler.addItemDecoration(new ItemOffsetDecoration(getResources().getDimensionPixelOffset(R.dimen.default_spacing_small)));
        homeRecyclerAdapter.setOnItemClickListener(new BaseSelectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                openActivity(ACTIVITY[position]);
            }
        });
    }

    private void runLayoutAnimation() {
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(mHomeRecycler.getContext(), R.anim.grid_layout_animation_scale);
        mHomeRecycler.setLayoutAnimation(controller);
        mHomeRecycler.getAdapter().notifyDataSetChanged();
        mHomeRecycler.scheduleLayoutAnimation();
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
            default:
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
