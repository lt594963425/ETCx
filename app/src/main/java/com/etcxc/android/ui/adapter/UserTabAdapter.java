package com.etcxc.android.ui.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Pair;

import java.util.List;

/**
 * Created by LiuTao on 2017/8/26 0026.
 */

public class UserTabAdapter extends FragmentPagerAdapter {
    List<Pair<String, Fragment>> items;
    public UserTabAdapter(FragmentManager fm, List<Pair<String, Fragment>> items) {
        super(fm);
        this.items =items;
    }

    @Override
    public Fragment getItem(int position) {
        return items.get(position).second;
    }

    @Override
    public int getCount() {
        return items.size();
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return items.get(position).first;
    }
}
