package org.example.cuican.Bean.auth;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户Token实体类
 * 存储用户登录后生成的JWT Token信息，用于身份认证和Token失效管理
 */
@Data
public class UserToken {

    /**
     * Token记录ID，主键自增
     */
    private Long id;

    /**
     * 关联的用户ID
     */
    private Long userId;

    /**
     * Token字符串，JWT格式
     */
    private String token;

    /**
     * Token类型：如 "Bearer"
     */
    private String tokenType;

    /**
     * Token过期时间，超过这个时间Token失效
     */
    private LocalDateTime expireTime;

    /**
     * Token创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 是否有效：1-有效可用，0-已失效（退出登录或过期）
     */
    private Integer isValid;
}
