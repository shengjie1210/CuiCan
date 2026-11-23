package org.example.cuican.Controller.tag;

import jakarta.annotation.Resource;
import org.example.cuican.Bean.Result;
import org.example.cuican.Service.tag.TagService;
import org.example.cuican.utils.UserContext;
import org.springframework.web.bind.annotation.*;

/**
 * 标签控制器
 * 提供标签查询和用户标签管理功能，用户可以添加/移除个人标签
 */
@RestController
@RequestMapping("/api/tags")
public class TagController {

    @Resource
    private TagService tagService;

    /**
     * 获取所有可用标签列表接口
     * 接口路径：/api/tags/all
     * 请求方式：GET
     * @return 统一响应结果，返回所有可用标签列表
     */
    @GetMapping("/all")
    public Result all() {
        return Result.success(tagService.getAvailableTags());
    }

    /**
     * 用户添加个人标签接口
     * 接口路径：/api/tags/user/add
     * 请求方式：POST
     * @param tagId 要添加的标签ID
     * @return 统一响应结果，添加成功返回成功消息，未登录返回错误
     */
    @PostMapping("/user/add")
    public Result add(@RequestParam Long tagId) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        tagService.addTag(userId, tagId);
        return Result.success();
    }

    /**
     * 用户移除个人标签接口
     * 接口路径：/api/tags/user/remove
     * 请求方式：POST
     * @param tagId 要移除的标签ID
     * @return 统一响应结果，移除成功返回成功消息，未登录返回错误
     */
    @PostMapping("/user/remove")
    public Result remove(@RequestParam Long tagId) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail("未登录");
        tagService.removeTag(userId, tagId);
        return Result.success();
    }
}
