package org.example.cuican.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码加密配置类
 * 注册BCrypt密码编码器到Spring容器，用于用户密码加密存储和登录密码校验
 */
@Configuration
public class PasswordConfig {
    /**
     * 注册BCrypt密码编码器Bean
     * BCrypt是一种自适应的密码加密算法，自动加盐，安全性高
     * 
     * @return BCryptPasswordEncoder实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
