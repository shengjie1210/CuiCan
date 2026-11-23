package org.example.cuican.Controller.auth;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import org.example.cuican.Bean.Result;
import org.example.cuican.Bean.auth.AuthUser;
import org.example.cuican.Bean.auth.LoginDTO;
import org.example.cuican.Bean.auth.RegisterDTO;
import org.example.cuican.Bean.auth.Role;
import org.example.cuican.Mapper.auth.RoleMapper;
import org.example.cuican.Mapper.auth.UserTokenMapper;
import org.example.cuican.Service.admin.SystemSettingService;
import org.example.cuican.Service.auth.AuthService;
import org.example.cuican.Service.captcha.CaptchaService;
import org.example.cuican.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 * 处理用户登录、管理员登录、用户注册、登出等身份认证相关接口
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserTokenMapper tokenMapper;

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private SystemSettingService systemSettingService;

    private static final String SECRET_KEY_SETTING = "admin_secret_key_h";
    private static final String RAW_KEY = "liujie_shengmin_1314";

    /**
     * 初始化方法，项目启动后执行
     * 初始化后台管理密钥的SHA256哈希到数据库，如果不存在则插入默认值
     */
    @PostConstruct
    public void initSecretKey() {
        if (systemSettingService.get(SECRET_KEY_SETTING) == null) {
            String hash = sha256(RAW_KEY);
            systemSettingService.set(SECRET_KEY_SETTING, hash, "后台管理密钥(SHA-256)");
        }
    }

    /**
     * 计算字符串的SHA256哈希值
     * @param input 输入字符串
     * @return SHA256哈希的十六进制字符串
     */
    private String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    /**
     * 普通用户登录接口
     * 接口路径：/auth/login
     * 请求方式：POST
     * @param dto 登录信息传输对象，包含用户名和密码
     * @return 统一响应结果，登录成功返回token和用户基本信息，失败返回错误信息
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO dto) {
        AuthUser user = authService.login(dto.getUsername(), dto.getPassword());
        if (user == null) return Result.fail("账号或密码错误");
        if (user.getStatus() != null && user.getStatus() == 0) return Result.fail("您的账号异常，请联系客服");
        Role role = roleMapper.selectById(user.getRoleId());
        String token = JwtUtil.generateToken(user.getId(), user.getRoleId(), role.getRoleLevel(), Long.parseLong(systemSettingService.get("security.token_expire_hours", "24")));
        authService.saveToken(user.getId(), token);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("role", role.getRoleLevel());
        data.put("avatar", user.getAvatar());
        return Result.success(data);
     }

    /**
     * 管理员后台登录接口
     * 需要额外验证管理密钥和验证码
     * 接口路径：/auth/admin/login
     * 请求方式：POST
     * @param dto 登录信息传输对象，包含用户名、密码、管理密钥、验证码信息
     * @return 统一响应结果，登录成功返回token和用户信息，失败返回错误信息
     */
     @PostMapping("/admin/login")
     public Result adminLogin(@RequestBody LoginDTO dto) {
        String rawKey = dto.getSecretKey();
        if (rawKey == null || rawKey.isEmpty()) {
            return Result.fail("请输入管理密钥");
        }
        String storedHash = systemSettingService.get(SECRET_KEY_SETTING);
        if (storedHash == null || !storedHash.equals(sha256(rawKey))) {
            return Result.fail("密钥错误");
        }

        if (dto.getCaptchaToken() != null && !dto.getCaptchaToken().isEmpty()) {
            boolean captchaOk = captchaService.verify(dto.getCaptchaToken(), dto.getCaptchaAnswer());
            if (!captchaOk) {
                return Result.fail("验证码错误");
            }
        }

        AuthUser user = authService.login(dto.getUsername(), dto.getPassword());
        if (user == null) return Result.fail("账号或密码错误");
        if (user.getStatus() != null && user.getStatus() == 0) return Result.fail("您的账号异常，请联系客服");

        Role role = roleMapper.selectById(user.getRoleId());
        if (role == null) {
            return Result.fail("角色不存在");
        }
        if (role.getRoleLevel() == null) {
            return Result.fail("角色权限等级未配置");
        }

        String token = JwtUtil.generateToken(user.getId(), user.getRoleId(), role.getRoleLevel(), Long.parseLong(systemSettingService.get("security.token_expire_hours", "24")));
        authService.saveToken(user.getId(), token);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("role", role.getRoleLevel());
        data.put("avatar", user.getAvatar());

        return Result.success(data);
    }

    /**
     * 用户注册接口
     * 接口路径：/auth/register
     * 请求方式：POST
     * @param dto 注册信息传输对象，包含用户名、密码、手机号等注册信息
     * @return 统一响应结果，注册成功直接返回登录token和用户信息，失败返回错误信息
     */
    @PostMapping("/register")
    public Result register(@RequestBody RegisterDTO dto) {
        AuthUser user = authService.register(dto);
        Role role = roleMapper.selectById(user.getRoleId());
        String token = JwtUtil.generateToken(user.getId(), user.getRoleId(), role.getRoleLevel(), Long.parseLong(systemSettingService.get("security.token_expire_hours", "24")));
        authService.saveToken(user.getId(), token);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("role", role.getRoleLevel());
        data.put("avatar", user.getAvatar());
        return Result.success(data);
    }

    /**
     * 用户登出接口
     * 使当前token失效，需要重新登录
     * 接口路径：/auth/logout
     * 请求方式：POST
     * @param request HTTP请求，从请求头获取token
     * @return 统一响应结果，登出成功返回成功消息
     */
    @PostMapping("/logout")
    public Result logout(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            tokenMapper.invalidateByToken(token);
        }
        return Result.success("退出成功");
    }
}
