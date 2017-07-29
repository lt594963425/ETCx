package com.etcxc.android.alipay;

/**
 * 支付宝回调code值
 * Created by caoyu on 2017/7/19.
 */

public class AliPay {

    public static final String PAY_OK = "9000";//支付宝支付成功
    public static final String PAY_PROCESSING_UNKNOWN = "8000";//正在处理中，支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
    public static final String PAY_FAIL = "4000";//支付宝支付失败
    public static final String PAY_REPEAT = "5000";//支付宝支付重复请求
    public static final String PAY_CANCEL = "6001";//用户中途取消
    public static final String PAY_NET_ERROR = "6002";//网络连接出错
    public static final String PAY_UNKNOWN = "6004";//支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
}
