package com.etcxc.android.ui.activity;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.etcxc.android.ui.fragment.FragmentExpand;
import com.etcxc.android.ui.fragment.FragmentHome;
import com.etcxc.android.ui.fragment.FragmentMine;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * 主界面Activity
 *
 * @author LiuTao
 * @date 2017/6/3 0003
 */

public class MainActivity extends BaseActivity implements TabHost.OnTabChangeListener {
    private ViewPager mViewPager;
    private FragmentTabHost mTabHost;
    int[] mTabImages = {
            R.drawable.tab_home_btn,
            R.drawable.tab_expand_btn,
            R.drawable.tab_mine_btn};
    String[] mTitles = {
            App.get().getString(R.string.index_home),
            App.get().getString(R.string.index_expand),
            App.get().getString(R.string.mime)};
    private VersionUpdateHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbarBack(false);
        initViewPagerFargment();
    }

    private void initViewPagerFargment() {
        mViewPager = find(R.id.pager);
        mTabHost = find(android.R.id.tabhost);
        mViewPager.setOffscreenPageLimit(4);
        FixPagerAdapter fixPagerAdapter = new FixPagerAdapter(getSupportFragmentManager());
        SparseArray<Fragment> fragmentsArr = new SparseArray<>();
        fragmentsArr.put(0, new FragmentHome());
        fragmentsArr.put(1, new FragmentExpand());
        fragmentsArr.put(2, new FragmentMine());
        fixPagerAdapter.setFragments(fragmentsArr);
        mViewPager.setAdapter(fixPagerAdapter);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.pager);
        mTabHost.setOnTabChangedListener(this);
        for (int i = 0; i < mTabImages.length; i++) {
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTitles[i]).setIndicator(getTabItemView(i));
            mTabHost.addTab(tabSpec, fragmentsArr.get(i).getClass(), null);
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
        View view = LayoutInflater.from(this).inflate(R.layout.main_tab_content, null, false);
        view.findViewById(R.id.tab_imageview).setBackgroundResource(mTabImages[i]);
        TextView mTextView = (TextView) view.findViewById(R.id.tab_textview);
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
        ImageView iv = mTabHost.getTabWidget()
                .getChildTabViewAt(position)
                .findViewById(R.id.tab_imageview);
        ObjectAnimator.ofFloat(iv, "rotation", 0.0F, 360.0F)
                .setDuration(250)
                .start();
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
}

