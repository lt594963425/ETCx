package com.etcxc.android.net;

/**
 * \
 * 接口清单
 * Created by caoyu on 2017/7/26.
 **/

public class FUNC {

    /**
     * 登录注册模块
     */
    public final static String REGISTER = "/xczx/user/register";
    public final static String VIRIFY_CODE = "/xczx/login/captcha"; //图形验证码
    public final static String LOGIN_PWD = "/xczx/login/loginpwd";
    public final static String LOGIN_SMS = "/xczx/login/loginSms";
    /**
     * 退出登录
     */
    public final static String LOGIN_OUT = "/xczx/user/loginOut";
    /**
     * 短信验证码
     */
    public final static String SMSREPORT = "/xczx/common/sendSms";

    /**
     * 密码（找回，修改）模块
     */
    public final static String RESET_PWD = "/xczx/user/resetPwd";
    public final static String MODIFYPWD = "/xczx/user/updatePwd";//


    /**
     * 用户信息（联系手机验证录入、修改手机号、修改昵称、头像等）模块
     */
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
    //上传申请证件
    public final static String UPLOAD_FUNC = "/xczx/issue/uploadLicense";
    //车主手机号码验证
    public final static String OWNERPHONE_VERIFY = "/xczx/issue/ownerPhone";
    //上传邮寄地址
    public final static String POST_INFO = "/xczx/issue/postInfo";
    //支付obu与预存金
    public final static String PAY_ISSUE = "/xczx/issue/pay";
    /**
     * 用户地址管理模块
     */

    //填写收货地址
    public final static String FUNC_POSTADDRESS = "/xczx/transaction/transactionmail";
    //获取收获地址
    public final static String FIND_POSTADDRESS = "/xczx/login/deliaddress";
    //修改收获地址
    public final static String RECEIPT_POSTADDRESS = "/xczx/login/deliaddressadd";
    //获取地址街道
    public final static String STREET = "/xczx/issue/getStreet";
    //选择地址获取省
    public final static String AREAPROVINCE = "/xczx/issue/getProvince";
    //选择地址获取县
    public final static String COUNTY = "/xczx/issue/getCounty";
    //选择地址获取城市
    public final static String CITY = "/xczx/issue/getCity";

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
    public final static String ADDCARD = "/xczx/rechargeable/addCardOrder";
    public final static String PAY = "/xczx/rechargeable/pay";
}
