package com.etcxc.android.base;

/**
 * 常量配置
 * Created by 刘涛 on 2017/7/8 0008.
 */

public class Constants {

    //appid 微信分配的公众账号ID
    public static final String WX_APP_ID = "wx21d6d90cd6a3a206";
    //商户ID
    public static final String WX_PARTNER_ID = "1482001172";
    //  微信 API密钥，在商户平台设置
    public static final String WX_APP_SECRET = "2e08ae5ae947e7bb99bfd32e24e1e7cd";
    /**
     * qq
     */
    public static final String QQ_APP_ID = "1106278726";
    public static final String QQ_APP_KEY = "18cyPpxYhCO0LUUK";
    //友盟
    public static final String UM_APP_KEY = "59683d1d07fe656c9a000787";
    /**
     * 微信
     */
    // 获取下单信息
    public final static String infoWXUrl = "http://192.168.6.126:9999/pay/pay/addcard/";
    //微信下单支付WX
    public final static String WXOrderUrl = "http://192.168.6.126:9999/pay/pay/payment/pay_message/";
    /**
     * PersonalInfoActivity
     */
    // PersonalInfoActivity 更换图形验证码url
    public final static String pictureCodeUrl = "http://192.168.6.58/login/login/captcha/code_key/";
    //用户登录url
    public final static String loginServerUrl = "http://192.168.6.58/login/login/login/";
    /**
     * MessageLoginActivity
     */
    //短信登录的url
    public final static String smsLoginServerUrl = "http://192.168.6.58/login/login/login/";
    //短信验证urlSMS
    public final static String SMSUrl = "http://192.168.6.58/login/sms/smsreport/tel/";
    /**
     * ResetPasswordActivity
     */
    public final static String ReSetSMSUrl = "http://192.168.6.58/user_information_modify/inf_modify_sms/smsreport/tel/";
    public final static String resetPwdUrl = "http://192.168.6.58/user_information_modify/user_information_modify/informationmodify/";
    /**
     * PhoneRegistActivity
     */
    public final static String loginSmsUrl = "http://192.168.6.58/register/register/register/";
    public final static String smsCodeUrl = "http://192.168.6.58/register/reg_sms/smsreport/tel/";
    //短信验证码统一接口
}
