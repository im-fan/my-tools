package com.my.security.lock.manager.util;

import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 对请求头数据MD5加密
 *
 *@author: Weiyf
 *@Date: 2018/9/18 16:55
 */
public class MD5Util {

    private static final String UTF8 = "utf-8";


    public static String md5String(String arg){

        String md5Arg = null;
        try{

            MessageDigest md5=MessageDigest.getInstance("MD5");
            BASE64Encoder base64en = new BASE64Encoder();
            /**加密后的字符串 **/
            md5Arg = base64en.encode(md5.digest(arg.getBytes(UTF8)));

        } catch (RuntimeException
                | NoSuchAlgorithmException
                | UnsupportedEncodingException e) {
        }
        return md5Arg;
    }

}
