package com.etcxc.android.net;

import static com.etcxc.android.net.NetConfig.HOST;

/**
 * Created by caoyu on 2017/7/26.
 */

public class Api {

    /**
     * 登录注册模块
     */
    // 手机短信注册
    public final static String loginSmsUrl = "http://192.168.6.58/register/register/register/";
    // PersonalInfoActivity 更换图形验证码url
    public final static String pictureCodeUrl = NetConfig.HOST+"/login/login/captcha/code_key/";
    //用户登录url
    public final static String loginServerUrl = "http://192.168.6.58/xczx/login/login/";
    //短信登录的url MessageLoginActivity
    public final static String smsLoginServerUrl = "http://192.168.6.58/login/login/login/";
    //短信验证urlSMS
    public final static String SMSUrl = "http://192.168.6.58/login/sms/smsreport/tel/";

    /**
     * 短信验证码模块
     */
    //找回密码验证码
    public final static String ReSetSMSUrl = "http://192.168.6.58/user_information_modify/inf_modify_sms/smsreport/tel/";
    //修改密码验证码
    public final static String newSmsCodeUrl = NetConfig.HOST + "/login/tel_change_sms/smsReport/new_tel/";
    //联系手机号 验证码
    public final static String FUNC_SEND_CODE = "/transaction/tran_sms/smsreport";
    //手机短信注册
    public final static String smsCodeUrl = "http://192.168.6.58/register/reg_sms/smsreport/tel/";

    /**
     * 密码（找回，修改）模块
     */
    //找回密码
    public final static String resetPwdUrl = "http://192.168.6.58/user_information_modify/user_information_modify/informationmodify/";
    //修改密码
    public final static String modifyPwdServerUrl =NetConfig.HOST+"/login/login/pwdchange/";


    /**
     * 用户手机号（联系手机验证录入、修改手机号）模块
     */
    //联系手机验证录入
    public final static String FUNC_COMMIT_CONTACT_PHONE = "/transaction/transaction/transactiontel";
    //修改手机号
    public final static String telChangeUrl = "/login/login/telchange";


    /**
     * etc发行模块
     */
    //etc 发行
    public final static String FUNC = "/transaction/transaction/transactionveh";
    //上传证件
    public final static String UPLOAD_FUNC = "/transaction/transaction/upload";

    /**
     * 用户地址管理模块
     */
    //填写收货地址
    public final static String FUNC_POSTADDRESS = "/transaction/transaction/transactionmail";
    //获取收获地址
    public final static String FUNC_FIND_POSTADDRESS = "/login/login/deliaddress";
    //修改收获地址
    public final static String FUNC_RECEIPT_POSTADDRESS = "/login/login/deliaddressadd";

    /**
     * 网点查询
     */
    //网点查询
    public final static String networkUrl =  NetConfig.HOST+"/xczx/login/networkstores";

    /**
     * 关于我们
     */
    //版本更新
    public final static String VERSION_FUNC = "/xczx/version_manage/versionmanage";
}
