package com.man.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;
import java.util.Base64.Decoder;

@Component
public class JWTUtil {
    /**
     * jwt过期时间 单位：秒
     */
    public static final long JWT_EXPTIME_SECONDS = 7200;

    /**
     * jwt存放用户信息key
     */
    public static final String SUBJECT = "sub";

    /**
     * jwt签名加密的密钥字符串
     */
    @Value("${secretKey}")
    private static String secretKey = "ZGFpbWxlcg==";


    /**
     * 签发JWT
     *
     * @param userId 用户ID
     * @return
     */
    public static String createJWTByUser(Long userId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(JWTUtil.SUBJECT, userId);
        return createJWT(map, JWTUtil.JWT_EXPTIME_SECONDS);
    }

    /**
     * 签发JWT
     *
     * @param claimsMap 参数map
     * @param ttlMillis 过期时间，单位：秒
     * @return
     */
    public static String createJWT(Map<String, Object> claimsMap, long ttlMillis) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        SecretKey secretKey = generalKey();
        JwtBuilder builder = Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setIssuer("Daimler builder")
                .setIssuedAt(now)
                .signWith(signatureAlgorithm, secretKey);
        if (!CollectionUtils.isEmpty(claimsMap)) {
            builder.setClaims(claimsMap);
        }
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis * 1000;
            Date expDate = new Date(expMillis);
            builder.setExpiration(expDate);
        }
        return builder.compact();
    }

    /**
     * 生成密钥
     *
     * @return
     */
    public static SecretKey generalKey() {
        Decoder decoder = Base64.getDecoder();
        byte[] encodedKey = decoder.decode(secretKey);
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }

    /**
     * 解析JWT字符串
     *
     * @param token
     * @return
     * @throws Exception
     */
    public static Claims parseJWT(String token) throws Exception {
        SecretKey secretKey = generalKey();
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从token中获取登录用户名
     */
    public String getUserNameFromToken(String token) {
        String username;
        try {
            Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    /**
     * 从token中获取JWT中的负载
     *
     * @throws Exception
     */
    private Claims getClaimsFromToken(String token) throws Exception {
        Claims claims = null;
        claims = parseJWT(token);
        return claims;
    }
}
