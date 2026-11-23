package org.example.cuican.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * JWT Token工具类
 * 提供JWT令牌的生成和解析功能，用于用户身份认证
 */
public class JwtUtil {
    /** JWT签名密钥，固定密钥用于HS256签名 */
    private static final String SECRET = "cui_can_secret_key_cui_can_secret_key_2026_secure";
    /** 转换后的签名密钥对象 */
    private static final Key key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    /**
     * 生成JWT令牌，默认有效期2天（48小时）
     * @param userId 用户ID
     * @param roleId 角色ID
     * @param roleLevel 角色等级（权限级别）
     * @return 生成的JWT令牌字符串
     */
    public static String generateToken(Long userId, Long roleId, Integer roleLevel) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("roleId", roleId)
                .claim("roleLevel", roleLevel)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 172800000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 生成JWT令牌，自定义过期时间
     * @param userId 用户ID
     * @param roleId 角色ID
     * @param roleLevel 角色等级（权限级别）
     * @param expireHours 过期时间，单位小时
     * @return 生成的JWT令牌字符串
     */
    public static String generateToken(Long userId, Long roleId, Integer roleLevel, long expireHours) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("roleId", roleId)
                .claim("roleLevel", roleLevel)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expireHours * 3600000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析JWT令牌，获取声明信息
     * 令牌无效或过期会抛出异常
     * @param token JWT令牌字符串
     * @return 解析后的Claims声明对象，包含userId、roleId等信息
     */
    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
