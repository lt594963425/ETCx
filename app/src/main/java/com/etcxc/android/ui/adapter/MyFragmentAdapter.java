package com.etcxc.android.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by 刘涛 on 2017/6/2 0002.
 */

public class MyFragmentAdapter extends FragmentPagerAdapter{
    List<Fragment> mlist;
    public MyFragmentAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.mlist = list;
    }

    @Override
    public Fragment getItem(int arg0) {
        return mlist.get(arg0);
    }

    @Override
    public int getCount() {
        return mlist == null ? 0 : mlist.size();
    }
}
