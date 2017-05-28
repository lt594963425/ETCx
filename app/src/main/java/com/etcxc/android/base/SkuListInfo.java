package com.etcxc.android.base;

import java.util.ArrayList;
import java.util.List;

/**
 * 结算数据的业务bean
 */

public class SkuListInfo {
    private static SkuListInfo skuListInfo;

    private SkuListInfo(){

    }

    public static SkuListInfo getInstance() {
        if (skuListInfo == null) {
            synchronized (SkuListInfo.class) {
                if (skuListInfo == null) {
                    skuListInfo = new SkuListInfo();
                }
            }
        }
        return skuListInfo;
    }

    public List<SkuInfo> list = new ArrayList<>();

    @Override
    public String toString() {
        //1:3:1,2,3,4|2:2:2,3
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            SkuInfo skuInfo = list.get(i);
                if(skuInfo.proNum != 0) {
                    stringBuffer.append(skuInfo.id + ":");
                    stringBuffer.append(skuInfo.proNum + ":");
                    for (int j = 0; j < skuInfo.productProperty.size(); j++) {
                        stringBuffer.append(skuInfo.productProperty.get(j));
                        if (j != skuInfo.productProperty.size() - 1) {
                            stringBuffer.append(",");
                        }
                    }
                }
            if (i != list.size() - 1 && list.get(i).proNum !=0 && list.get(i + 1).proNum != 0) {
                stringBuffer.append("|");
            }
        }
        return stringBuffer.toString();
    }
}
