/**
 * Copyright 2017 bejson.com
 */
package com.etcxc.android.bean;

import java.util.Date;

/**
 *2017-06-16 11:3:44
 */
public class Var {

    private String tel;
    private String pwd;
    private Date login_time;
    private String nick_name;

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getTel() {
        return tel;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getPwd() {
        return pwd;
    }

    public void setLogin_time(Date login_time) {
        this.login_time = login_time;
    }

    public Date getLogin_time() {
        return login_time;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getNick_name() {
        return nick_name;
    }

}