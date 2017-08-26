package com.etcxc.android.base;

/**
 * 常量配置
 * Created by 刘涛 on 2017/7/8 0008.
 */

public class Constants {
    //在线发行支付 标识issue
    public static boolean ETC_ISSUE = false;
    //appid 微信分配的公众账号ID
    public static final String WX_APP_ID = "wx21d6d90cd6a3a206";
    //商户ID
    public static final String MCH_ID = "1482001172";
    //  微信 API密钥，在商户平台设置
    public static final String WX_APP_KEY = "2e08ae5ae947e7bb99bfd32e24e1e7cd";
    //微信统一下单接口
    public static final String UNIFIED_ORDER = "https://api.mch.weixin.qq.com/pay/unifiedorder";


    /**
     * qq
     */
    public static final String QQ_APP_ID = "1106278726";
    public static final String QQ_APP_KEY = "18cyPpxYhCO0LUUK";
    //友盟
    public static final String UM_APP_KEY = "59683d1d07fe656c9a000787";


    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;

    public static final int DISTANCE = 1;
    public static final int LOCATION = 0;
    public static final String FLAG = "flag";


    public static final String PACKAGE_NAME =
            "com.etcxc.android";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String RESULT_ADDRESS = PACKAGE_NAME + ".RESULT_ADDRESS";
    public static final String LOCATION_LATITUDE_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_LATITUDE_DATA_EXTRA";
    public static final String LOCATION_LONGITUDE_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_LONGITUDE_DATA_EXTRA";
    public static final String LOCATION_NAME_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_NAME_DATA_EXTRA";
    public static final String FETCH_TYPE_EXTRA = PACKAGE_NAME + ".FETCH_TYPE_EXTRA";

}
