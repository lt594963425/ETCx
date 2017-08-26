package com.etcxc.android.ui.activity;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Pair;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.ui.adapter.UserTabAdapter;
import com.etcxc.android.ui.fragment.FragmenTotal;
import com.etcxc.android.ui.fragment.FragmentActiva;
import com.etcxc.android.ui.fragment.FragmentCanceled;
import com.etcxc.android.ui.fragment.FragmentInUse;
import com.etcxc.android.ui.fragment.FragmentReport;

import java.util.ArrayList;
import java.util.List;


/**
 * 我的卡
 * Created by LiuTao on 2017/8/26 0026.
 */

public class UserCardActivity extends BaseActivity implements TabLayout.OnTabSelectedListener {
    private Toolbar mToolbar;
    private TabLayout mTab;
    private ViewPager mViewPager;
    private List<Pair<String, Fragment>> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_etc_card);
        initView();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.user_card_toolbar);
        mToolbar.setTitle(R.string.user_etc_card);
        mTab = (TabLayout) findViewById(R.id.user_card_tab);
        mViewPager = (ViewPager) findViewById(R.id.user_card_viewpager);
        items = new ArrayList<>();
        items.add(new Pair<>("全部", new FragmenTotal()));
        items.add(new Pair<>("使用中", new FragmentInUse()));
        items.add(new Pair<>("待激活", new FragmentActiva()));
        items.add(new Pair<>("挂失中", new FragmentReport()));
        items.add(new Pair<>("已注销", new FragmentCanceled()));
        mViewPager.setAdapter(new UserTabAdapter(getSupportFragmentManager(), items));
        mTab.setupWithViewPager(mViewPager);
        mTab.addOnTabSelectedListener(this);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
