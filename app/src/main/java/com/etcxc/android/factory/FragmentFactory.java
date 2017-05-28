package com.etcxc.android.factory;

import com.etcxc.android.base.BaseFragment;
import com.etcxc.android.fragment.HomeFragment;
import com.etcxc.android.fragment.MineFragment;
import com.etcxc.android.fragment.TwoFragment;

import java.util.HashMap;

public class FragmentFactory {
    private static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_SEARCH = 1;
    private static final int FRAGMENT_CLASS = 2;

    public static HashMap<Integer, BaseFragment> mCacheFragments = new HashMap<>();

    public static BaseFragment createFragment(int position) {
        BaseFragment fragment = null;

        if (mCacheFragments.containsKey(position)) {
            fragment = mCacheFragments.get(position);
            return fragment;
        }
        switch (position) {
            case FRAGMENT_HOME:
                fragment = new HomeFragment();
                break;
            case FRAGMENT_SEARCH:
                fragment = new TwoFragment();
                break;
            case FRAGMENT_CLASS:
                fragment = new MineFragment();
                break;
        }
        mCacheFragments.put(position, fragment);
        return fragment;
    }
}
