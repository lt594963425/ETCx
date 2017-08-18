package com.etcxc.android.net;

/**\
 * 接口清单
 * Created by caoyu on 2017/7/26.
 */

public class FUNC {

    /**
     * 登录注册模块
     */
    public final static String REGISTER = "/xczx/register/register/";
    public final static String VIRIFY_CODE = "/login/login/captcha/code_key/";
    public final static String LOGIN_PWD = "/xczx/login/loginpwd";

    /**
     * 获取验证码
     * 1-登录 2-注册  3-修改信息  4-办理  5-号码变更
     */
    public final static String SEND_SMS = "/xczx/sms/sendSms";

    /**
     * 密码（找回，修改）模块
     */
    public final static String INFORMATIONMODIFY = "/xczx/user_information_modify/informationmodify/";
    public final static String MODIFYPWD = "/xczx/login/pwdchange/";


    /**
     * 用户手机号（联系手机验证录入、修改手机号）模块
     */
    //联系手机验证录入
    public final static String FUNC_COMMIT_CONTACT_PHONE = "/transaction/transaction/transactiontel";
    //修改手机号
    public final static String TELCHANGE = "/xczx/login/telchange";


    /**
     * etc发行模块
     */
    //etc 发行
    public final static String FUNC = "/xczx/transaction/transactionveh";
    //上传证件
    public final static String UPLOAD_FUNC = "/transaction/transaction/upload";

    /**
     * 用户地址管理模块
     */
    //填写收货地址
    public final static String FUNC_POSTADDRESS = "/xczx/transaction/transactionmail";
    //获取收获地址
    public final static String FIND_POSTADDRESS = "/xczx/login/deliaddress";
    //修改收获地址
    public final static String RECEIPT_POSTADDRESS = "/xczx/login/deliaddressadd";
    public final static String AREAPROVINCE = "/xczx/transaction/areaprovince/";
    public final static String COUNTY = "/xczx/transaction/areastreet/county/";
    public final static String PROVINCE = "/xczx/transaction/areacity/province/";
    public final static String CITY = "/xczx/transaction/areacounty/city/";

    /**
     * 网点查询
     */
    //网点查询
    public final static String NETWORK = "/xczx/login/networkstores";

    /**
     * 关于我们
     */
    //版本更新
    public final static String VERSION_FUNC = "/xczx/version_manage/versionmanage";

    /**
     * ETC 充值
     */
    public final static String ADDCARD = "/xczx/pay/addcard/";
    //微信下单支付
    public final static String WXORDER = "/xczx/pay/payment/";
}
