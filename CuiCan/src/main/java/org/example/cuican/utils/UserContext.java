package org.example.cuican.utils;

/**
 * 用户上下文工具类
 * 使用ThreadLocal存储当前请求线程的用户信息，方便在业务层获取当前登录用户
 * 每个请求处理完成后需要清理，避免内存泄漏
 */
public class UserContext {
    /** 当前线程用户ID存储 */
    private static final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();
    /** 当前线程用户权限等级存储 */
    private static final ThreadLocal<Integer> roleLevelHolder = new ThreadLocal<>();

    /**
     * 设置当前线程的用户信息
     * @param userId 当前登录用户ID
     * @param roleLevel 当前用户权限等级
     */
    public static void set(Long userId, Integer roleLevel) {
        userIdHolder.set(userId);
        roleLevelHolder.set(roleLevel);
    }

    /**
     * 获取当前登录用户ID
     * @return 当前登录用户ID，未登录时返回null
     */
    public static Long getUserId() {
        return userIdHolder.get();
    }

    /**
     * 获取当前用户权限等级
     * @return 当前用户权限等级，未登录时返回null
     */
    public static Integer getRoleLevel() {
        return roleLevelHolder.get();
    }

    /**
     * 清理当前线程的用户信息
     * 请求处理完成后调用，防止内存泄漏
     */
    public static void clear() {
        userIdHolder.remove();
        roleLevelHolder.remove();
    }
}
