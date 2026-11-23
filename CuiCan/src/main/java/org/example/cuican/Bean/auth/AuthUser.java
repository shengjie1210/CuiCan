package org.example.cuican.Bean.auth;

import lombok.Data;

/**
 * 认证用户信息实体类
 * 用于存储认证系统中的用户账号信息，包括用户名、密码、角色关联和账号状态
 */
@Data
public class AuthUser {
    /**
     * 用户ID，主键自增
     */
    private Long id;

    /**
     * 用户名，登录账号，唯一标识
     */
    private String username;

    /**
     * 加密后的密码，BCrypt加密
     */
    private String password;

    /**
     * 关联角色ID，每个用户对应一个角色
     */
    private Long roleId;

    /**
     * 用户头像URL
     */
    private String avatar;

    /**
     * 账号状态：1-正常可用，0-禁用不可用
     */
    private Integer status;
}
