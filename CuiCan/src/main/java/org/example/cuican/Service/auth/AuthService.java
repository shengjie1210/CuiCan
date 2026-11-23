package org.example.cuican.Service.auth;

import org.example.cuican.Bean.auth.AuthUser;
import org.example.cuican.Bean.auth.RegisterDTO;

/**
 * 认证服务接口
 * 处理用户登录、注册、token保存等认证相关业务
 */
public interface AuthService {
    /**
     * 用户登录验证
     * @param username 用户名
     * @param password 密码明文
     * @return 认证成功返回认证用户信息，失败返回null
     */
    AuthUser login(String username, String password);

    /**
     * 保存用户token到数据库，标记为有效
     * @param userId 用户ID
     * @param token 生成的JWT token
     */
    void saveToken(Long userId, String token);

    /**
     * 用户注册
     * @param dto 注册信息DTO
     * @return 注册成功返回认证用户信息
     */
    AuthUser register(RegisterDTO dto);
}
