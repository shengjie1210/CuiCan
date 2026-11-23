package org.example.cuican.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security配置类
 * 由于项目使用自定义JWT拦截器实现认证，所以关闭Spring Security默认的安全控制
 * 将认证授权完全交给自定义拦截器处理
 */
@Configuration
public class SecurityConfig {
    /**
     * 配置Spring Security安全过滤链
     * 1. 关闭CSRF防护，前后端分离项目不需要
     * 2. 关闭默认表单登录页面，使用自定义登录接口
     * 3. 关闭HTTP Basic认证
     * 4. 所有请求放行，认证工作交给自定义JWT拦截器处理
     * 
     * @param http HttpSecurity构建对象
     * @return 配置好的安全过滤链
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // 关闭csrf（否则POST请求会被拦截，前后端分离不需要）
                .csrf(csrf -> csrf.disable())

                // 关闭Spring Security默认登录页面
                .formLogin(form -> form.disable())

                // 关闭HTTP Basic认证
                .httpBasic(basic -> basic.disable())

                // 所有请求放行，认证交给自定义JWT拦截器处理
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .build();
    }
}
