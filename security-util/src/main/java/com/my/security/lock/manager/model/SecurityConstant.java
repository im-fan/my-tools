package com.my.security.lock.manager.model;

import java.util.concurrent.TimeUnit;

/**
 * 常量
 *
 *@author: Weiyf
 *@Date: 2018/9/19 17:13
 */
public class SecurityConstant {

    /** 请求头参数名称**/
    public static final String HeaderParam = "MD-Authorization";

    /** jwt过期时间 3分钟**/
    public static final long JwtRefreshTtl = TimeUnit.MILLISECONDS.convert(3, TimeUnit.MINUTES);

}
