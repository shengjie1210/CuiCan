package org.example.cuican.Bean.auth;

import lombok.Data;

/**
 * 登录请求数据传输对象(DTO)
 * 接收前端发来的登录请求数据，包括用户名、密码、验证码等信息
 */
@Data
public class LoginDTO {
    /**
     * 用户名，登录账号
     */
    private String username;

    /**
     * 密码，用户输入的明文密码
     */
    private String password;

    /**
     * 管理后台登录密钥，仅管理员登录需要
     */
    private String secretKey;

    /**
     * 验证码Token，用于验证码校验
     */
    private String captchaToken;

    /**
     * 图形验证码答案，普通图形验证码用户输入
     */
    private String captchaAnswer;
}
