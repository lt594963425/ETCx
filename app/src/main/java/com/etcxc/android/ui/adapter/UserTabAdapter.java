package com.etcxc.android.ui.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Pair;

import java.util.List;

/**
 * 我的卡管理适配器
 * Created by LiuTao on 2017/8/26 0026.
 */

public class UserTabAdapter extends FragmentPagerAdapter {
    List<Pair<String, Fragment>> mItems;
    public UserTabAdapter(FragmentManager fm, List<Pair<String, Fragment>> items) {
        super(fm);
        this.mItems =items;
    }

    @Override
    public Fragment getItem(int position) {
        return mItems.get(position).second;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return mItems.get(position).first;
    }
}
