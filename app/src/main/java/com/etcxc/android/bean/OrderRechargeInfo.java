/**
 * Copyright 2017 bejson.com
 */
package com.etcxc.android.bean;

import java.io.Serializable;

/**
 *2017-06-16 11:3:44
 */
public class OrderRechargeInfo implements Serializable {
    //充值人的名字
    private String rechargename;
    //车牌号
    private String  licenseplate;
    //充值卡号
    private String etccarnumber;
    //充值金额
    private int rechargemoney;

    public String getLicenseplate() {
        return licenseplate;
    }

    public void setLicenseplate(String licenseplate) {
        this.licenseplate = licenseplate;
    }

    public int getRechargemoney() {
        return rechargemoney;
    }

    public void setRechargemoney(int rechargemoney) {
        this.rechargemoney = rechargemoney;
    }



    public String getRechargename() {
        return rechargename;
    }

    public void setRechargename(String rechargename) {
        this.rechargename = rechargename;
    }



    public String getEtccarnumber() {
        return etccarnumber;
    }

    public void setEtccarnumber(String etccarnumber) {
        this.etccarnumber = etccarnumber;
    }

    @Override
    public String toString() {
        return "OrderRechargeInfo{" +
                "rechargename='" + rechargename + '\'' +
                ", licenseplate='" + licenseplate + '\'' +
                ", etccarnumber='" + etccarnumber + '\'' +
                ", rechargemoney='" + rechargemoney + '\'' +
                '}';
    }
}