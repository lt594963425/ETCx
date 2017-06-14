package com.etcxc.android.ui.activity;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.ui.adapter.MyFragmentAdapter;
import com.etcxc.android.ui.fragment.Fragment1;
import com.etcxc.android.ui.fragment.Fragment2;
import com.etcxc.android.utils.UIUtils;

import java.util.ArrayList;

/**
 * 主界面Activity
 */

public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener, TabHost.OnTabChangeListener {
    private ViewPager mViewPager;
    private Toolbar mToolbar;
    private FragmentTabHost mTabHost;

    private static final int ERROR = 1;
    private static final int SUCCESS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initState();
        initView();
        initPage();
    }
    private void initView() {
        mViewPager = find(R.id.pager);
        mViewPager.addOnPageChangeListener(this);
        //让ViewPager切换到第1个页面
        mViewPager.setCurrentItem(0, false);
        mToolbar = find(R.id.My_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(getString(R.string.index_page) + "ETC");
        mTabHost = find(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.pager);
        mTabHost.setOnTabChangedListener(this);
        initTabs();
    }

    private void initTabs() {
        Class mFragmentArray[] = {Fragment1.class, Fragment2.class};
        int mImageViewArray[] = {R.drawable.tab_home_btn, R.drawable.tab_view_btn};
        String mTextViewArray[] = {getString(R.string.index_page), getString(R.string.mime)};
        int count = mTextViewArray.length;
        for (int i = 0; i < count; i++) {
            TextView textView = new TextView(this);
            textView.setText(mTextViewArray[i]);
            Drawable d = getResources().getDrawable(mImageViewArray[i]);
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            textView.setCompoundDrawables(null, d, null, null);
            textView.setCompoundDrawablePadding(UIUtils.dip2Px(8));
            textView.setPadding(0, UIUtils.dip2Px(8), 0, UIUtils.dip2Px(8));
            textView.setGravity(Gravity.CENTER);
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextViewArray[i]).setIndicator(textView);
            mTabHost.addTab(tabSpec, mFragmentArray[i], null);
        }
    }

    Fragment1 f1;
    Fragment2 f2;

    private void initPage() {
        ArrayList<Fragment> list = new ArrayList();
        f1 = new Fragment1();
        f2 = new Fragment2();
        list.add(f1);
        list.add(f2);
        //绑定Fragment适配器
        mViewPager.setAdapter(new MyFragmentAdapter(getSupportFragmentManager(), list));
        mTabHost.getTabWidget().setDividerDrawable(null);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        TabWidget widget = mTabHost.getTabWidget();
        int oldFocusability = widget.getDescendantFocusability();
        widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);//设置View覆盖子类控件而直接获得焦点
        mTabHost.setCurrentTab(position);//根据位置Postion设置当前的Tab
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
                mToolbar.setTitle(getString(R.string.index_page) + "ETC");
                break;
            case 1:
                mToolbar.setTitle(getString(R.string.mime) + "ETC");
                break;
        }
    }
    /**
     * 沉浸式状态栏
     */
    private void initState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }
}

