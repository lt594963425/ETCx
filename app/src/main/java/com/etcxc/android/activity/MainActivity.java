package com.etcxc.android.activity;


import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.base.BaseFragment;
import com.etcxc.android.base.LoadingPager;
import com.etcxc.android.factory.FragmentFactory;
import com.etcxc.android.net.OkClient;
import com.etcxc.android.utils.LogUtil;
import com.etcxc.android.utils.UIUtils;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabSelectListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends BaseActivity implements View.OnClickListener {
    //保存底部按键位置对应的按键
    private Map<Integer, Integer> tabIdPosition = new HashMap<>();
    private BottomBarTab mNearby;
    private BottomBar mBottomBar;
    private ViewPager mVpContent;
    private Toolbar mToolbar;
    TextView mToolbarTitle;
    private MyPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     /*   //取消顶部标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        initViews();
        initData();
    }

    private void initViews() {
        find(R.id.tv_register).setOnClickListener(this);
        mBottomBar = find(R.id.bottomBar);
        mVpContent = find(R.id.vp_content);
        mToolbar = find(R.id.toolbar);
        mToolbarTitle = find(R.id.toolbar_title);
        tabIdPosition.put(R.id.tab_recents, 0);
        tabIdPosition.put(R.id.tab_favorites, 1);
        tabIdPosition.put(R.id.tab_nearby, 2);
        mAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mVpContent.setAdapter(mAdapter);
        //toolbar初始化
        //底部标签栏
        initBottomBar();
    }

    /**
     * 初始化底部标签栏
     */
    private void initBottomBar() {
        mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                mVpContent.setCurrentItem(tabIdPosition.get(tabId), false);
            }
        });
        mNearby = mBottomBar.getTabWithId(R.id.tab_friends);
    }

    private void initData() {
        /**
         * 监听fragment页面改变,以触发加载数据
         */
        final ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {
                //触发加载数据
                LoadingPager loadingPager = FragmentFactory.createFragment(position).getLoadingPager();
                //滑动ViewPager改变底部标签页
                mBottomBar.selectTabAtPosition(position);
                switch (position) {
                    case 0:
                        mToolbar.setBackgroundColor(UIUtils.getColor(R.color.colorAccent));
                        mToolbarTitle.setText("首页");
                        loadingPager.triggerLoadData();
                        break;
                    case 1:
                        mToolbar.setBackgroundColor(UIUtils.getColor(R.color.colorSearch));
                        mToolbarTitle.setText("搜索");
                        loadingPager.triggerLoadData();
                        break;
                    case 2:
                        mToolbar.setBackgroundColor(UIUtils.getColor(R.color.colorCategory));
                        mToolbarTitle.setText("我的");
                        loadingPager.triggerLoadData();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        };
        mVpContent.setOnPageChangeListener(onPageChangeListener);

        /**
         * 默认加载第一页的数据
         */
        mVpContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                onPageChangeListener.onPageSelected(0);
                mVpContent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_register:
                register();
                break;
        }
    }

    private void testNet() {
        Flowable.just("https://api.github.com/users/xwpeng/repos")
                .map(new Function<String, String>() {
                    @Override
                    public String apply(@NonNull String s) throws Exception {
                        return OkClient.get(s, new JSONObject());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        LogUtil.d("xwpeng16", s);
                    }
                });
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            BaseFragment fragment = FragmentFactory.createFragment(position);
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    private void register() {
      /*  Flowable.just("").map(new Function<String, String>() {
            @Override
            public String apply(@NonNull String s) throws Exception {

            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        LogUtil.d("xwpeng16", s);
                    }
                });*/
    }

}

