package com.etcxc.android.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 刘涛 on 2017/7/8 0008.
 */

public class WxPayRecharge {

    /**
     * code : s_ok
     * "var": {
     * "appid": "wx21d6d90cd6a3a206",
     * "noncestr": "1c8b9be7bb9380430edb110177bdb659",
     * "package": ""Sign=WXPay"",
     * "partnerid": "1482001172",
     * "prepayid": "wx201708241628243abae114370990218222",
     * "sign": "753FD27EA3C86104EE1F0D9127F7FEAC",
     * "timestamp": 1503563311
     * }
     */

    private String code;
    private VarBean var;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public VarBean getVar() {
        return var;
    }

    public void setVar(VarBean var) {
        this.var = var;
    }

    public static class VarBean {
        /**
         * appid : wx21d6d90cd6a3a206
         * partnerid : 1482001172
         * noncestr : 1c8b9be7bb9380430edb110177bdb659
         * prepayid : wx201708241628243abae114370990218222
         * package : Sign=WXPay
         * timestamp : 1503563311
         * sign : 753FD27EA3C86104EE1F0D9127F7FEAC
         */
        private String appid;
        private String partnerid;
        private String noncestr;
        private String prepayid;
        @SerializedName("package")
        private String packageX;
        private int timestamp;
        private String sign;

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getPartnerid() {
            return partnerid;
        }

        public void setPartnerid(String partnerid) {
            this.partnerid = partnerid;
        }

        public String getNoncestr() {
            return noncestr;
        }

        public void setNoncestr(String noncestr) {
            this.noncestr = noncestr;
        }

        public String getPrepayid() {
            return prepayid;
        }

        public void setPrepayid(String prepayid) {
            this.prepayid = prepayid;
        }

        public String getPackageX() {
            return packageX;
        }

        public void setPackageX(String packageX) {
            this.packageX = packageX;
        }

        public int getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(int timestamp) {
            this.timestamp = timestamp;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        @Override
        public String toString() {
            return "VarBean{" +
                    "appid='" + appid + '\'' +
                    ", partnerid='" + partnerid + '\'' +
                    ", noncestr='" + noncestr + '\'' +
                    ", prepayid='" + prepayid + '\'' +
                    ", packageX='" + packageX + '\'' +
                    ", timestamp=" + timestamp +
                    ", sign='" + sign + '\'' +
                    '}';
        }
    }

}
