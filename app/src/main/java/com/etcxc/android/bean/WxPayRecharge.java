package com.etcxc.android.bean;

/**
 * Created by 刘涛 on 2017/7/8 0008.
 */

public class WxPayRecharge {
    private String appid;
    private String nonceStr;// 随机字符串，不长于32位，服务器鑫哥生成
    private String partnerId;// 微信支付分配的商户号
    private String prepayId;// 预支付订单号，app服务器调用“统一下单”接口获取
    private String packageValue;// 固定值Sign=WXPay，可以直接写死，服务器返回的也是这个固定值
    private String timeStamp;// 时间戳，app服务器鑫哥给出
    private String sign;  //// 签名，服务器鑫哥给出

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getPrepayId() {
        return prepayId;
    }

    public void setPrepayId(String prepayId) {
        this.prepayId = prepayId;
    }

    public String getPackageValue() {
        return packageValue;
    }

    public void setPackageValue(String packageValue) {
        this.packageValue = packageValue;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "WxPayRecharge{" +
                "appid='" + appid + '\'' +
                ", nonceStr='" + nonceStr + '\'' +
                ", partnerId='" + partnerId + '\'' +
                ", prepayId='" + prepayId + '\'' +
                ", packageValue='" + packageValue + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
