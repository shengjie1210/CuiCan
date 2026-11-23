package org.example.cuican.Controller.admin;

import jakarta.annotation.Resource;
import org.example.cuican.Bean.Result;
import org.example.cuican.Service.user.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员用户管理控制器
 * 提供后台用户管理功能，包括用户列表查询、用户账号状态修改（封禁/解禁）
 */
@RestController
@RequestMapping("/admin/user")
public class AdminUserController {

    @Resource
    private UserService userService;

    @Resource
    private org.example.cuican.Mapper.user.UserMapper userMapper;

    /**
     * 查询所有用户列表接口
     * 接口路径：/admin/user/list
     * 请求方式：GET
     * @return 统一响应结果，返回所有用户完整信息列表
     */
    @GetMapping("/list")
    public Result list() {
        return Result.success(userService.listAll());
    }

    /**
     * 修改用户账号状态接口
     * 接口路径：/admin/user/status
     * 请求方式：POST
     * @param userId 用户ID
     * @param userStatus 新状态：0-禁用封禁，1-正常启用
     * @return 统一响应结果，修改成功返回成功；禁止禁用最高管理员返回失败
     */
    @PostMapping("/status")
    public Result status(Long userId, Integer userStatus) {
        org.example.cuican.Bean.user.User u = userMapper.selectById(userId);
        if (u == null) return Result.fail("用户不存在");
        if (userStatus == 0 && u.getRoleId() == 3) {
            return Result.fail("禁止操作");
        }
        userService.setStatus(userId, userStatus);
        return Result.success();
    }
}
