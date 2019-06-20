package com.my.security.lock.manager.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.my.security.lock.manager.model.TokenDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;

/**
 * 生成jwt信息
 *
 *@author: Weiyf
 *@Date: 2018/9/19 16:53
 */
public class JwtUtil {


    private static final String JwtId = "MDJwt";

    /** MDJwt md5加密**/
    public static final String JwtSecret = "7b3f21a0b067839c8357aa82c53dbdaf";

    private static final SecretKey secretKey;

    static {
        byte[] encodedKey = Base64.decodeBase64(JwtSecret);
        secretKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
    }

    /**
     * 创建jwt
     *
     * @param subject
     * @param ttlMillis
     * @return
     * @throws Exception
     */
    public static String createJWT(Object subject, long ttlMillis) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        JwtBuilder builder = Jwts.builder()
                .setId(JwtId)
                .setIssuedAt(now)
                .setSubject(JSONObject.toJSONString(subject))
                .signWith(signatureAlgorithm, secretKey);
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }
        return builder.compact();
    }


    /**
     * 解析JwtAccountToken对象
     * @param jwtToken
     * @return
     */
    public static TokenDto parseTokenDto(String jwtToken) {
        Claims claims = parseJWT(jwtToken);
        if (claims == null) {
            return null;
        }
        return JSON.parseObject(claims.getSubject(), TokenDto.class);
    }

    /**
     * 解密jwt
     *
     * @param jwtToken
     * @return
     */
    private static Claims parseJWT(String jwtToken) {
        try{
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(jwtToken).getBody();
            return claims;
        } catch (RuntimeException e){
            return null;
        }
    }

}
