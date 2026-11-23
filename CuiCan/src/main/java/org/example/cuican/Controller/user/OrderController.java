package org.example.cuican.Controller.user;

import jakarta.annotation.Resource;
import org.example.cuican.Bean.Result;
import org.example.cuican.Bean.user.CreateOrderDTO;
import org.example.cuican.Service.user.OrderService;
import org.example.cuican.utils.UserContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户端订单控制器
 * 提供用户订单相关功能：订单列表查询、创建订单、订单详情、取消订单、确认收货、模拟支付
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Resource
    private OrderService orderService;

    /**
     * 获取当前用户订单列表接口
     * 接口路径：/order/list
     * 请求方式：GET
     * @return 统一响应结果，返回当前用户的所有订单列表
     */
    @GetMapping("/list")
    public Result list() {
        Long userId = UserContext.getUserId();
        return Result.success(orderService.list(userId));
    }

    /**
     * 创建订单接口
     * 接口路径：/order/create
     * 请求方式：POST
     * @param dto 创建订单DTO，包含收货地址ID、勾选的购物车项ID列表
     * @return 统一响应结果，创建成功返回新订单ID
     */
    @PostMapping("/create")
    public Result create(@RequestBody CreateOrderDTO dto) {
        Long userId = UserContext.getUserId();
        Long orderId = orderService.createOrder(userId, dto);
        return Result.success(orderId);
    }

    /**
     * 获取订单详情接口
     * 接口路径：/order/detail
     * 请求方式：GET
     * @param orderId 订单ID
     * @return 统一响应结果，返回订单详细信息，包含订单项列表
     */
    @GetMapping("/detail")
    public Result detail(Long orderId) {
        return Result.success(orderService.detail(orderId));
    }

    /**
     * 用户取消订单接口
     * 接口路径：/order/cancel
     * 请求方式：POST
     * @param orderId 要取消的订单ID
     * @return 统一响应结果，取消成功返回成功消息
     * 取消订单会释放库存，只有未支付订单可以取消
     */
    @PostMapping("/cancel")
    public Result cancel(Long orderId) {
        Long userId = UserContext.getUserId();
        orderService.cancelOrder(userId, orderId);
        return Result.success();
    }

    /**
     * 用户确认收货接口
     * 接口路径：/order/confirm
     * 请求方式：POST
     * @param orderId 要确认收货的订单ID
     * @return 统一响应结果，确认成功返回成功消息
     * 只有已发货订单可以确认收货
     */
    @PostMapping("/confirm")
    public Result confirm(Long orderId) {
        Long userId = UserContext.getUserId();
        orderService.confirmOrder(userId, orderId);
        return Result.success();
    }

    /**
     * 模拟支付订单接口
     * 接口路径：/order/pay
     * 请求方式：PUT
     * @param method 支付方式：alipay/wechat/card/balance
     * @param orderId 要支付的订单ID
     * @return 统一响应结果，支付成功返回成功消息
     * @note 这是模拟支付接口，实际项目中需要对接真实支付网关
     */
    @PutMapping("/pay")
    public Result pay(@RequestParam(required = false) String method, Long orderId) {
        Long userId = UserContext.getUserId();
        orderService.payOrder(userId, orderId, method);
        return Result.success();
    }
}
