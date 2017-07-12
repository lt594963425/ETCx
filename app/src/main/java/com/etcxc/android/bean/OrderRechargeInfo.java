/**
 * Copyright 2017 bejson.com
 */
package com.etcxc.android.bean;

/**
 *2017-06-16 11:3:44
 */
public class OrderRechargeInfo {

    private String rechargename;
    private String carnumber;
    private String etccarnumber;
    private String rechargemoney;
    private String alloney;

    public String getAlloney() {
        return alloney;
    }

    public String getRechargemoney() {
        return rechargemoney;
    }

    public void setRechargemoney(String rechargemoney) {
        this.rechargemoney = rechargemoney;
    }

    public void setAlloney(String alloney) {
        this.alloney = alloney;
    }

    public String getRechargename() {
        return rechargename;
    }

    public void setRechargename(String rechargename) {
        this.rechargename = rechargename;
    }

    public String getCarnumber() {
        return carnumber;
    }

    public void setCarnumber(String carnumber) {
        this.carnumber = carnumber;
    }

    public String getEtccarnumber() {
        return etccarnumber;
    }

    public void setEtccarnumber(String etccarnumber) {
        this.etccarnumber = etccarnumber;
    }

}