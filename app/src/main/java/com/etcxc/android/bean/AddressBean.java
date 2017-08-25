package com.etcxc.android.bean;

/**
 * 获取省
 * Created by LiuTao on 2017/8/24 0024.
 */

public class AddressBean {
    String name ;
    String code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "AddressBean{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
