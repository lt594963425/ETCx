package com.etcxc.android.utils;

import com.etcxc.android.bean.Networkstore;

import java.util.Comparator;

/**
 * 网点距离比较
 * Created by caoyu on 2017/8/3.
 */

public class DistanceLowToHighComparator implements Comparator<Networkstore.VarBean> {
    @Override
    public int compare(Networkstore.VarBean o1, Networkstore.VarBean o2) {
        double price1 = 0;
        double price2 = 0;

        if(o1.getDistance() != null){
            price1 = o1.getDistance();
        }

        if(o2.getDistance() != null){
            price2 = o2.getDistance();
        }

        if(price1 < price2){//price1排在price2前面
            return -1;
        }
        else if(price1 > price2){//
            return 1;
        }
        else{
            return 0;
        }
    }
}
