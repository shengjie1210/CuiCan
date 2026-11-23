package org.example.cuican.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.cuican.Bean.auth.UserToken;
import org.example.cuican.Mapper.auth.UserTokenMapper;
import org.example.cuican.utils.JwtUtil;
import org.example.cuican.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.example.cuican.Mapper.user.UserMapper;
import java.time.LocalDateTime;

/**
 * JWT认证拦截器
 * 实现接口请求的身份认证和权限校验，对需要登录的接口进行token验证
 * 将解析出的用户信息存入UserContext方便后续业务使用
 */
@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private UserTokenMapper tokenMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 判断请求URI是否需要登录认证
     * @param uri 请求URI路径
     * @return true 需要登录，false 不需要登录可匿名访问
     * 匹配规则：所有 /admin/、/api/admin/、/api/user/ 等接口都需要登录
     */
    private boolean needLogin(String uri) {
        if (uri.startsWith("/admin/")) return true;
        if (uri.startsWith("/api/admin/")) return true;
        return uri.startsWith("/api/user/")
                || uri.startsWith("/api/order/")
                || uri.startsWith("/api/cart/")
                || uri.startsWith("/favorite/")
                || uri.startsWith("/address/")
                || uri.startsWith("/order/");
    }

    /**
     * 拦截器前置处理，在进入Controller之前进行身份认证
     * 1. 对于不需要登录的接口，尝试解析token如果成功则设置用户上下文，失败也不拦截
     * 2. 对于需要登录的接口，完整校验token：是否存在、是否有效、是否过期
     * 3. 校验通过后将用户ID和角色等级存入UserContext
     * 4. 额外校验用户账号状态是否正常，封禁用户禁止访问
     * 
     * @param request HTTP请求
     * @param response HTTP响应
     * @param handler 处理器
     * @return true 放行，false 拦截
     * @throws java.io.IOException IO异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws java.io.IOException {

        String uri = request.getRequestURI();

        // 不需要登录的接口，仍然尝试解析token，挂了也不影响（dev友好）
        if (!needLogin(uri)) {
            String header = request.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                try {
                    String t = header.substring(7);
                    Claims claims = JwtUtil.parseToken(t);
                    Long uid = Long.parseLong(claims.getSubject());
                    Integer rl = Integer.valueOf(claims.get("roleLevel").toString());
                    UserContext.set(uid, rl);
                } catch (Exception ignored) {}
            }
            return true;
        }

        String header = request.getHeader("Authorization");

       if (header == null || !header.startsWith("Bearer ")) {
           log.warn("未登录访问: uri={}", uri);
           unauthorized(response, "未登录");
           return false;
       }

       String token = header.substring(7);
       UserToken dbToken = tokenMapper.selectByToken(token);

       if (dbToken == null || dbToken.getIsValid() == 0) {
           log.warn("token失效: uri={}", uri);
           unauthorized(response, "token失效");
           return false;
       }
       if (dbToken.getExpireTime().isBefore(LocalDateTime.now())) {
           log.warn("token过期: uri={}", uri);
           unauthorized(response, "token过期");
           return false;
       }

        Claims claims = JwtUtil.parseToken(token);
        Long userId = Long.parseLong(claims.getSubject());
        Integer roleLevel = Integer.valueOf(claims.get("roleLevel").toString());

        UserContext.set(userId, roleLevel);
        org.example.cuican.Bean.user.User u = userMapper.selectById(userId);
        if (u != null && u.getStatus() != null && u.getStatus() == 0) {
            log.warn("被封禁用户尝试访问: userId={}", userId);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "您的账号异常，请联系客服");
            UserContext.clear();
            return false;
        }
        log.info("鉴权成功: userId={}, role={}", userId, roleLevel);

        return true;
    }

    /**
     * 请求完成后的后置处理
     * 记录请求完成日志，关键是清除ThreadLocal中的用户信息，避免内存泄漏和用户信息串线
     * 
     * @param request HTTP请求
     * @param response HTTP响应
     * @param handler 处理器
     * @param ex 异常对象，如果请求过程中发生异常会携带过来
     */
    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        String uri = request.getRequestURI();
        Long userId = UserContext.getUserId();
        if (ex != null) {
            log.error("请求异常结束: uri={}, userId={}, error={}", uri, userId, ex.getMessage());
        } else {
            log.info("请求完成: uri={}, userId={}", uri, userId);
        }
        UserContext.clear();
    }

    /**
     * 管理员权限校验工具类
     * 提供静态方法给Controller进行方法级权限校验
     */
    public static class AdminGuard {
        /**
         * 要求管理员权限
         * @param roleLevel 当前用户角色等级
         * @throws RuntimeException 如果权限不足抛出异常
         */
        public static void requireAdmin(Integer roleLevel) {
            if (roleLevel == null || roleLevel < 2) {
                throw new RuntimeException("需要管理员权限");
            }
        }

        /**
         * 要求超级管理员权限
         * @param roleLevel 当前用户角色等级
         * @throws RuntimeException 如果权限不足抛出异常
         */
        public static void requireSuper(Integer roleLevel) {
            if (roleLevel == null || roleLevel < 3) {
                throw new RuntimeException("需要超级管理员权限");
            }
        }
    }

    /**
     * 发送401未授权响应
     * @param resp HTTP响应对象
     * @param msg 错误信息
     * @throws java.io.IOException IO异常
     */
    private void unauthorized(jakarta.servlet.http.HttpServletResponse resp, String msg) throws java.io.IOException {
        resp.setStatus(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write("{\"code\":401,\"message\":\"" + msg + "\"}");
    }
}
