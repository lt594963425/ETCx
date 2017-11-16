package com.etcxc.android.ui.factory;


import com.etcxc.android.base.BaseFragment;
import com.etcxc.android.ui.fragment.FragmentExpand;
import com.etcxc.android.ui.fragment.FragmentHome;
import com.etcxc.android.ui.fragment.FragmentMine;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一管理所有fragment
 * @author ${LiuTao}
 * @date 2017/11/16/016
 */

public class FragmentFactory {
    /**
     * 首页
     */
    public static final int TAB_Home = 0;
    /**
     * 拓展
     */
    public static final int TAB_EXPAND = 1;
    /**
     * 我的
     */
    public static final int TAB_MY = 2;

    private static Map<Integer, BaseFragment> mMainFragmentMap = new HashMap<>();

    /**
     * 统一通过一个方法创建Fragment，避免重复创建
     * @param index
     * @return
     */
    public static BaseFragment createMainFragment(int index) {
        BaseFragment fragment = mMainFragmentMap.get(index);
        if (fragment == null) {
            switch (index) {
                case TAB_Home:
                    fragment = new FragmentHome();
                    break;
                case TAB_EXPAND:
                    fragment = new FragmentExpand();
                    break;
                case TAB_MY:
                    fragment = new FragmentMine();
                    break;
                default:
                    break;
            }
            mMainFragmentMap.put(index, fragment);
        }
        return fragment;
    }
}
