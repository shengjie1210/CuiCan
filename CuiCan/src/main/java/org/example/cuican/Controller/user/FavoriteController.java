package org.example.cuican.Controller.user;

import jakarta.annotation.Resource;
import org.example.cuican.Bean.Result;
import org.example.cuican.Service.user.FavoriteService;
import org.example.cuican.utils.UserContext;
import org.springframework.web.bind.annotation.*;

/**
 * 用户收藏控制器
 * 提供商品收藏功能：查看收藏列表、添加收藏、检查是否已收藏、移除收藏
 */
@RestController
@RequestMapping("/favorite")
public class FavoriteController {

    @Resource
    private FavoriteService favoriteService;

    /**
     * 获取当前用户收藏列表接口
     * 接口路径：/favorite/list
     * 请求方式：GET
     * @return 统一响应结果，返回当前用户的所有收藏商品列表
     */
    @GetMapping("/list")
    public Result list() {
        Long userId = UserContext.getUserId();
        return Result.success(favoriteService.list(userId));
    }

    /**
     * 添加商品到收藏接口
     * 接口路径：/favorite/add
     * 请求方式：POST
     * @param productId 要收藏的商品ID
     * @return 统一响应结果，添加成功返回成功消息
     */
    @PostMapping("/add")
    public Result add(@RequestParam Long productId) {
        favoriteService.add(productId);
        return Result.success();
    }

    /**
     * 检查商品是否已收藏接口
     * 接口路径：/favorite/check
     * 请求方式：GET
     * @param productId 商品ID
     * @return 统一响应结果，返回true表示已收藏，false表示未收藏
     */
    @GetMapping("/check")
    public Result check(@RequestParam Long productId) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.success(false);
        return Result.success(favoriteService.check(userId, productId));
    }

    /**
     * 按商品ID移除收藏接口
     * 接口路径：/favorite/remove
     * 请求方式：POST
     * @param productId 要移除收藏的商品ID
     * @return 统一响应结果，移除成功返回成功消息
     */
    @PostMapping("/remove")
    public Result remove(@RequestParam Long productId) {
        Long userId = UserContext.getUserId();
        favoriteService.removeByProductId(userId, productId);
        return Result.success();
    }

    /**
     * 按收藏记录ID删除收藏接口
     * 接口路径：/favorite/delete
     * 请求方式：POST
     * @param id 收藏记录ID
     * @return 统一响应结果，删除成功返回成功消息
     */
    @PostMapping("/delete")
    public Result delete(Long id) {
        favoriteService.delete(id);
        return Result.success();
    }
}
