package com.etcxc.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.etcxc.android.R;
import com.etcxc.android.base.App;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.helper.VersionUpdateHelper;
import com.etcxc.android.net.download.DownloadOptions;
import com.etcxc.android.ui.adapter.MyFragmentAdapter;
import com.etcxc.android.ui.fragment.FragmentExpand;
import com.etcxc.android.ui.fragment.FragmentHome;
import com.etcxc.android.ui.fragment.FragmentMine;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * 主界面Activity
 * Created by LiuTao on 2017/6/3 0003.
 */

public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener, TabHost.OnTabChangeListener {
    private ViewPager mViewPager;
    private FragmentTabHost mTabHost;
    Class mFragmentArray[] = {
            FragmentHome.class,
            FragmentExpand.class,
            FragmentMine.class};
    int mImageViewArray[] = {
            R.drawable.tab_home_btn,
            R.drawable.tab_expand_btn,
            R.drawable.tab_mine_btn};
    String mTextViewArray[] = {
            App.get().getString(R.string.index_home),
            App.get().getString(R.string.index_expand),
            App.get().getString(R.string.mime)};
    private VersionUpdateHelper mHelper;
    private MyFragmentAdapter mAdapter;
    private FragmentMine mFragmentMine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initPage();
        //mHelper = new VersionUpdateHelper(this);
        //mHelper.checkVersion();
    }

    private void initView() {
        setToolbarBack(false);
        mViewPager = find(R.id.pager);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setCurrentItem(0, false);
        mViewPager.setOffscreenPageLimit(3);
        mTabHost = find(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.pager);
        mTabHost.setOnTabChangedListener(this);
        initTabs();
    }

    private void initTabs() {
        int count = mTextViewArray.length;
        for (int i = 0; i < count; i++) {
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextViewArray[i]).setIndicator(getTabItemView(i));
            mTabHost.addTab(tabSpec, mFragmentArray[i], null);
            mTabHost.setTag(i);
            mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.selector_tab_background);//背景状态
        }
    }


    public View getTabItemView(int i) {
        View view = LayoutInflater.from(this).inflate(R.layout.main_tab_content, null);
        ImageView mImageView = (ImageView) view.findViewById(R.id.tab_imageview);
        TextView mTextView = (TextView) view.findViewById(R.id.tab_textview);
        mImageView.setBackgroundResource(mImageViewArray[i]);
        mTextView.setText(mTextViewArray[i]);
        return view;
    }


    private void initPage() {
        ArrayList<Fragment> list = new ArrayList<>();
        mFragmentMine = new FragmentMine();
        list.add(new FragmentHome());
        list.add(new FragmentExpand());
        list.add(mFragmentMine);
        mAdapter = new MyFragmentAdapter(getSupportFragmentManager(), list);
        mViewPager.setAdapter(mAdapter);
        mTabHost.getTabWidget().setDividerDrawable(null);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }
    @Override
    public void onPageSelected(int position) {
        TabWidget widget = mTabHost.getTabWidget();
        int oldFocusability = widget.getDescendantFocusability();
        widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        mTabHost.setCurrentTab(position);
        widget.setDescendantFocusability(oldFocusability);//设置取消分割线
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onTabChanged(String tabId) {
        int position = mTabHost.getCurrentTab();
        mViewPager.setCurrentItem(position, false);
        switch (position) {
            case 0:
                showToobar();
                setTitle(R.string.app_name);
                break;
            case 1:
                setTitle(R.string.expand);
                showToobar();
                startRoationAnim(position);
                break;
            case 2:
                hindToobar();
                setTitle(R.string.mime);
                break;
        }
    }

    private void startRoationAnim(int position) {
        ImageView iv = (ImageView) mTabHost
                .getTabWidget()
                .getChildTabViewAt(position)
                .findViewById(R.id.tab_imageview);
        Animation anim = new RotateAnimation(
                0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setFillAfter(true);
        anim.setRepeatCount(1);
        anim.setFillAfter(true);
        anim.setDuration(250);
        anim.setInterpolator(new LinearInterpolator());
        iv.startAnimation(anim);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mFragmentMine.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DownloadOptions options) {
        mHelper.downloadPd(options);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

