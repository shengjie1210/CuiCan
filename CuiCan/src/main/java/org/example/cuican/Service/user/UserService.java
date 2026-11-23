package org.example.cuican.Service.user;

import org.example.cuican.Bean.user.User;
import org.example.cuican.Bean.user.UserProfile;
import java.util.List;

/**
 * 用户服务接口
 * 提供用户信息查询、用户列表查询、状态修改功能
 */
public interface UserService {
    /**
     * 获取指定用户个人主页信息
     * @param userId 用户ID
     * @return 用户个人主页信息，包含基本信息、扩展信息、标签、照片等
     */
    UserProfile getUserProfile(Long userId);

    /**
     * 获取所有用户列表
     * @return 所有用户列表
     */
    List<User> listAll();

    /**
     * 修改用户账号状态
     * @param userId 用户ID
     * @param status 新状态：0-封禁，1-正常
     */
    void setStatus(Long userId, Integer status);
}
