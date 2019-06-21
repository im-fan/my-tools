package com.my.util.jwt;

import java.util.concurrent.TimeUnit;

public class JwtConstant {

    /**
     * jwt
     */
    public static final String JWT_ID = "jwt";
    public static final String JWT_SECRET = "7786df7fc3a34e26a61c034d5ec8245d";

    //过期时间 356天
    public static final long JWT_REFRESH_TTL = TimeUnit.MILLISECONDS.convert(365, TimeUnit.DAYS);

}
