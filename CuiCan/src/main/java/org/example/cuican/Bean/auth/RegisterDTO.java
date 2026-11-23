package org.example.cuican.Bean.auth;

import lombok.Data;

/**
 * 用户注册请求数据传输对象(DTO)
 * 接收前端发来的用户注册信息，包括用户名、密码、手机号、验证码等
 */
@Data
public class RegisterDTO {
    /**
     * 用户名，注册的账号
     */
    private String username;

    /**
     * 密码，用户设置的明文密码
     */
    private String password;

    /**
     * 手机号，用户绑定的手机号码
     */
    private String phone;

    /**
     * 验证码Token，用于验证码校验
     */
    private String captchaToken;

    /**
     * 图形验证码答案，用户输入的验证码
     */
    private String captchaAnswer;
}
