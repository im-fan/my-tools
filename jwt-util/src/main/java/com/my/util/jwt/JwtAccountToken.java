package com.my.util.jwt;


import java.io.Serializable;

public class JwtAccountToken implements Serializable {

    // 账号id
    private Integer userId;

    /** JwtToken版本号，当App需要所有用户都重新登录时则修改版本号，以0.1递增  默认1.0 程序中设置*/
    // 版本
    private double version;

    public JwtAccountToken(){}

    public Integer getUserId() {

        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public double getVersion() {
        return version;
    }

    public void setVersion(double version) {
        this.version = version;
    }
}
