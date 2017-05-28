package com.etcxc.android.bean;

import java.util.List;

/**
 * 商品列表的业务bean
 */

public class ProductListInfo {


    public int listCount;//商品总数
    public String response;//服务器返回的响应
    public List<ListFilterBean> listFilter;//筛选属性
    public List<?> productList;//得到产品列表

    public static class ListFilterBean {


        public String key;//属性名称
        public List<ValueListBean> valueList;//得到排序的列表

        public static class ValueListBean {

            public String id;//标识
            public String name;//属性名称
        }
    }

    @Override
    public String toString() {
        return "ProductListInfo{" +
                "listCount=" + listCount +
                ", response='" + response + '\'' +
                ", listFilter=" + listFilter +
                ", productList=" + productList +
                '}';
    }
}
