package com.etcxc.android.net;

import static com.etcxc.android.net.NetConfig.HOST;

/**
 * Created by caoyu on 2017/7/26.
 */

public class Api {
    /**
     * PersonalInfoActivity
     */
    // PersonalInfoActivity 更换图形验证码url
    public final static String pictureCodeUrl = NetConfig.HOST+"/login/login/captcha/code_key/";
    //用户登录url
    public final static String loginServerUrl = "http://192.168.6.58/xczx/login/login/";
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
     * ChangePasswordActivity  修改密码的
     */

    public final static String modifyPwdServerUrl =NetConfig.HOST+"/login/login/pwdchange/";

    /**
     * ChangePhoneActivity 获取验证码 修改手机号
     */
    public final static String newSmsCodeUrl = NetConfig.HOST + "/login/tel_change_sms/smsReport/new_tel/";
    public final static String telChangeUrl = "/login/login/telchange";

    /**
     * ContactPhoneActivity 联系手机验证录入
     */
    public final static String FUNC_SEND_CODE = "/transaction/tran_sms/smsreport";
    public final static String FUNC_COMMIT_CONTACT_PHONE = "/transaction/transaction/transactiontel";

    /**
     * ETCIssueActivity etc发行
     */
    public final static String FUNC = "/transaction/transaction/transactionveh";

    /**
     * PhoneRegistActivity 手机短信注册页面
     */
    public final static String loginSmsUrl = "http://192.168.6.58/register/register/register/";
    public final static String smsCodeUrl = "http://192.168.6.58/register/reg_sms/smsreport/tel/";
    //短信验证码统一接口

    /**
     * PostAddressActivity 收货地址
     */
    public final static String FUNC_POSTADDRESS = "/transaction/transaction/transactionmail";

    /**
     * ReceiptAddressActivity 修改地址
     */
    public final static String FUNC_RECEIPT_POSTADDRESS = "/login/login/deliaddressadd";
    public final static String FUNC_FIND_POSTADDRESS = "/login/login/deliaddress";

    /**
     * UploadLicenseActivity 上传证件
     */
    public final static String UPLOAD_FUNC = "/transaction/transaction/upload";
    /**
     * NetworkQueryActivity 网点查询
     */
    public final static String networkUrl =  NetConfig.HOST+"/xczx/login/networkstores";
}
