package org.example.cuican.Controller.user;

import org.example.cuican.Bean.Result;
import org.example.cuican.Bean.user.UserProfile;
import org.example.cuican.Service.user.UserService;
import org.springframework.web.bind.annotation.*;

/**
 * 用户信息控制器
 * 提供公开的用户信息查询接口
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取指定用户个人主页信息接口
     * 接口路径：/user/profile/{id}
     * 请求方式：GET
     * @param id 用户ID
     * @return 用户详情信息，包含基本信息、扩展信息、标签、照片等
     */
    @GetMapping("/profile/{id}")
    public UserProfile getProfile(@PathVariable Long id) {
        return userService.getUserProfile(id);
    }
}
