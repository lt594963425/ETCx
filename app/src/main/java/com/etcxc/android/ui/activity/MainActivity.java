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
import com.etcxc.android.ui.adapter.FixPagerAdapter;
import com.etcxc.android.ui.factory.FragmentFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


/**
 * 主界面Activity
 *
 * @author LiuTao
 * @date 2017/6/3 0003
 */

public class MainActivity extends BaseActivity implements TabHost.OnTabChangeListener {
    private ViewPager mViewPager;
    private FragmentTabHost mTabHost;
    int mTabImages[] = {
            R.drawable.tab_home_btn,
            R.drawable.tab_expand_btn,
            R.drawable.tab_mine_btn};
    String mTitles[] = {
            App.get().getString(R.string.index_home),
            App.get().getString(R.string.index_expand),
            App.get().getString(R.string.mime)};
    private VersionUpdateHelper mHelper;
    private List<Fragment> mFragments;
    private FixPagerAdapter mFixPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbarBack(false);
        initViewPagerFargment();
    }

    private void initViewPagerFargment() {
        mViewPager = findViewById(R.id.pager);
        mTabHost = findViewById(android.R.id.tabhost);
        mViewPager.setOffscreenPageLimit(4);
        mFixPagerAdapter = new FixPagerAdapter(getSupportFragmentManager());
        mFragments = new ArrayList<>();
        for (int i = 0; i < mTitles.length; i++) {
            mFragments.add(FragmentFactory.createMainFragment(i));
        }
        mFixPagerAdapter.setFragments(mFragments);
        mViewPager.setAdapter(mFixPagerAdapter);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.pager);
        mTabHost.setOnTabChangedListener(this);
        for (int i = 0; i < mTabImages.length; i++) {
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTitles[i]).setIndicator(getTabItemView(i));
            mTabHost.addTab(tabSpec, mFragments.get(i).getClass(), null);
            mTabHost.setTag(i);
            mTabHost.getTabWidget().getChildAt(i)
                    .setBackgroundResource(R.drawable.selector_tab_background);
        }
        mTabHost.getTabWidget().setDividerDrawable(null);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                TabWidget widget = mTabHost.getTabWidget();
                int oldFocusability = widget.getDescendantFocusability();
                widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
                widget.setDescendantFocusability(oldFocusability);//设置取消分割线
                mTabHost.setCurrentTab(position);
            }
        });
    }

    private View getTabItemView(int i) {
        View view = LayoutInflater.from(this).inflate(R.layout.main_tab_content, null);
        view.findViewById(R.id.tab_imageview).setBackgroundResource(mTabImages[i]);
        TextView mTextView = view.findViewById(R.id.tab_textview);
        mTextView.setText(mTitles[i]);
        return view;
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
            default:
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
        mFragments.get(2).onActivityResult(requestCode, resultCode, data);
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

