package org.example.cuican.Controller.user;

import jakarta.annotation.Resource;
import org.example.cuican.Bean.Result;
import org.example.cuican.Mapper.user.CartMapper;
import org.example.cuican.Service.user.CartService;
import org.example.cuican.utils.UserContext;
import org.springframework.web.bind.annotation.*;

/**
 * 购物车控制器
 * 提供用户购物车管理功能：添加商品、修改数量、勾选、删除等
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Resource
    private CartService cartService;

    @Resource
    private CartMapper cartMapper;

    /**
     * 获取当前用户购物车中已勾选的商品接口
     * 接口路径：/api/cart/checked
     * 请求方式：GET
     * @return 统一响应结果，返回已勾选的购物车商品列表，用于下单
     */
    @GetMapping("/checked")
    public Result checked() {
        Long userId = UserContext.getUserId();
        return Result.success(cartMapper.selectCheckedByUserId(userId));
    }

    /**
     * 获取当前用户购物车列表接口
     * 接口路径：/api/cart/list
     * 请求方式：GET
     * @return 统一响应结果，返回当前用户的完整购物车列表
     */
    @GetMapping("/list")
    public Result list() {
        Long userId = UserContext.getUserId();
        return Result.success(cartService.list(userId));
    }

    /**
     * 添加商品到购物车接口
     * 接口路径：/api/cart/add
     * 请求方式：POST
     * @param productId 要添加的商品ID
     * @param quantity 添加数量，默认1件
     * @return 统一响应结果，添加成功返回成功消息
     */
    @PostMapping("/add")
    public Result add(@RequestParam Long productId,
                      @RequestParam(defaultValue = "1") Integer quantity) {
        Long userId = UserContext.getUserId();
        cartService.add(userId, productId, quantity);
        return Result.success();
    }

    /**
     * 修改购物车商品数量接口
     * 接口路径：/api/cart/update
     * 请求方式：POST
     * @param cartId 购物车记录ID
     * @param quantity 新的数量
     * @return 统一响应结果，修改成功返回成功消息
     */
    @PostMapping("/update")
    public Result update(@RequestParam Long cartId, @RequestParam Integer quantity) {
        cartService.updateQuantity(cartId, quantity);
        return Result.success();
    }

    /**
     * 切换购物车商品勾选状态接口
     * 接口路径：/api/cart/check
     * 请求方式：POST
     * @param cartId 购物车记录ID
     * @return 统一响应结果，切换成功返回成功消息
     * @note 如果当前是勾选状态则取消勾选，如果是未勾选则勾选
     */
    @PostMapping("/check")
    public Result check(@RequestParam Long cartId) {
        cartService.toggleCheck(cartId);
        return Result.success();
    }

    /**
     * 删除购物车商品接口
     * 接口路径：/api/cart/delete
     * 请求方式：POST
     * @param cartId 要删除的购物车记录ID
     * @return 统一响应结果，删除成功返回成功消息
     */
    @PostMapping("/delete")
    public Result delete(@RequestParam Long cartId) {
        cartService.delete(cartId);
        return Result.success();
    }
}
