package com.my.util.jwt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.my.util.jwt.JwtAccountToken;
import com.my.util.jwt.JwtConstant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;

public class JwtUtil {

    private static final SecretKey secretKey;

    static {
        byte[] encodedKey = Base64.decodeBase64(JwtConstant.JWT_SECRET);
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
                .setId(JwtConstant.JWT_ID)
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
     * 解密jwt
     *
     * @param jwtToken
     * @return
     */
    public static Claims parseJWT(String jwtToken) {
        try{
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(jwtToken).getBody();
            return claims;
        } catch (RuntimeException e){
            return null;
        }
    }

    /**
     * 解析JwtAccountToken对象
     * @param jwtToken
     * @return
     */
    public static JwtAccountToken parseJwtAccountToken(String jwtToken) {
        Claims claims = parseJWT(jwtToken);
        if (claims == null) {
            return null;
        }
        return JSON.parseObject(claims.getSubject(), JwtAccountToken.class);
    }

    /**
     * 解析对象
     * @param jwtToken
     * @return
     */
    public static <T> T parseObject(String jwtToken, Class<T>  objectType) {
        Claims claims = parseJWT(jwtToken);
        if (claims == null) {
            return null;
        }
        return JSON.parseObject(claims.getSubject(),objectType);
    }

}
