package org.example.cuican.Service.Impl.auth;

import lombok.extern.slf4j.Slf4j;
import org.example.cuican.Bean.auth.AuthUser;
import org.example.cuican.Bean.auth.RegisterDTO;
import org.example.cuican.Service.captcha.CaptchaService;
import org.example.cuican.Bean.auth.UserToken;
import org.example.cuican.Bean.user.User;
import org.example.cuican.Mapper.auth.AuthUserMapper;
import org.example.cuican.Mapper.auth.UserTokenMapper;
import org.example.cuican.Service.auth.AuthService;
import org.example.cuican.Service.admin.SystemSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

/**
 * 认证服务实现类
 * 实现用户登录、注册、token保存等认证相关业务逻辑
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthUserMapper userMapper;

    @Autowired
    private UserTokenMapper tokenMapper;

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SystemSettingService systemSettingService;

    /**
     * 用户登录验证
     * 根据用户名查询用户，验证密码是否正确
     * @param username 用户名
     * @param password 密码明文
     * @return 认证成功返回认证用户信息
     * @throws RuntimeException 用户名不存在或密码错误时抛出异常
     */
    @Override
    public AuthUser login(String username, String password) {
        log.info("登录开始: {}", username);
        AuthUser user = userMapper.selectByUsername(username);
        if (user == null) {
            log.warn("用户不存在: {}", username);
            throw new RuntimeException("用户不存在");
        }
        boolean match = passwordEncoder.matches(password, user.getPassword());
        if (!match) {
            log.error("密码错误: {}", username);
            throw new RuntimeException("密码错误");
        }
        log.info("登录成功: {}", username);
        return user;
    }

    /**
     * 用户注册
     * 检查注册开关、验证码、用户名唯一性，创建新用户入库
     * @param dto 注册信息DTO
     * @return 注册成功返回认证用户信息
     * @throws RuntimeException 各种验证失败抛出异常
     */
    @Override
    public AuthUser register(RegisterDTO dto) {
        log.info("注册开始: {}", dto.getUsername());

        // 0a. 检查注册开关
        String regEnabled = systemSettingService.get("security.registration_enabled", "true");
        if (!"true".equalsIgnoreCase(regEnabled)) {
            log.warn("注册功能已关闭");
            throw new RuntimeException("注册功能暂未开放");
        }

        // 0b. 校验验证码
        boolean captchaOk = captchaService.verify(dto.getCaptchaToken(), dto.getCaptchaAnswer());
        if (!captchaOk) {
            log.warn("验证码错误: {}", dto.getUsername());
            throw new RuntimeException("验证码错误");
        }

        // 1. 检查用户名
        AuthUser exist = userMapper.selectByUsername(dto.getUsername());
        if (exist != null) {
            log.warn("用户名已存在: {}", dto.getUsername());
            throw new RuntimeException("用户名已存在");
        }

        // 2. 生成ID
        Long maxId = userMapper.selectMaxId();
        Long newId = (maxId == null ? 1000L : maxId) + 1;

        // 3. 创建用户
        String defaultAvatar = systemSettingService.get("member.default_avatar", "/avatar/default.png");
        User user = new User();
        user.setId(newId);
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPhone(dto.getPhone());
        user.setGender("未知");
        user.setMemberLevel(1);
        user.setTotalSpent(0.0);
        user.setBalance(0.0);
        user.setStatus(1);
        user.setRoleId(1L);
        user.setAvatar(defaultAvatar);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 4. 入库
        userMapper.insert(user);
        log.info("注册成功: id={}, username={}", newId, dto.getUsername());
        return userMapper.selectByUsername(dto.getUsername());
    }

    /**
     * 保存用户token到数据库
     * 先使该用户之前所有token失效，再插入新token
     * @param userId 用户ID
     * @param token 生成的JWT token
     */
    @Override
    public void saveToken(Long userId, String token) {
        log.info("生成Token: userId={}", userId);
        tokenMapper.invalidateByUserId(userId);
        UserToken t = new UserToken();
        t.setUserId(userId);
        t.setToken(token);
        t.setTokenType("ACCESS");
        long expireHours = Long.parseLong(systemSettingService.get("security.token_expire_hours", "24"));
        t.setExpireTime(LocalDateTime.now().plusHours(expireHours));
        t.setCreatedTime(LocalDateTime.now());
        t.setIsValid(1);
        tokenMapper.insert(t);
    }
}
