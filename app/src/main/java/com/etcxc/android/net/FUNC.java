package com.etcxc.android.net;

/**\
 * 接口清单
 * Created by caoyu on 2017/7/26.
 */

public class FUNC {

    /**
     * 登录注册模块
     */
    public final static String REGISTER = "/xczx/user/register";
    public final static String VIRIFY_CODE = "/xczx/login/captcha"; //图形验证码
    public final static String LOGIN_PWD = "/xczx/login/loginpwd";
    public final static String LOGIN_SMS = "/xczx/login/loginSms";

    /**
     * 短信验证码
     */
    public final static String SMSREPORT = "/xczx/common/sendSms";

    /**
     * 密码（找回，修改）模块
     */
    public final static String INFORMATIONMODIFY = "/xczx/user_information_modify/informationmodify/";
    public final static String MODIFYPWD = "/xczx/user/updatePwd";//


    /**
     * 用户信息（联系手机验证录入、修改手机号、修改昵称、头像等）模块
     */
    //联系手机验证录入
    public final static String FUNC_COMMIT_CONTACT_PHONE = "/transaction/transaction/transactiontel";
    //更换手机号
    public final static String TELCHANGE = "/xczx/user/updateTel";
   //昵称

    public final static String NICKNAME_CHANGE = "/xczx/user/updateNickName";
    //获取用户头像
    public final static String GET_HEAD = "/xczx/user/getHeadImage";
    //修改头像
    public final static String HEAD_CHANGE = "/xczx/user/updateHeadImage?tel=17375851914&token=a93f62aef94251d098636086c91714d8";


    /**
     * etc申请模块
     */
    //第一步，车辆是否可申请
    public final static String CAN_ISSUE = "/xczx/issue/canIssue";
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
    public final static String NETWORK = "/xczx/common/networkStores";

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
