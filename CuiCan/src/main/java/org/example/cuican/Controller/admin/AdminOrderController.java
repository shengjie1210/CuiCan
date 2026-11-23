package org.example.cuican.Controller.admin;

import jakarta.annotation.Resource;
import org.example.cuican.Bean.Result;
import org.example.cuican.config.JwtInterceptor.AdminGuard;
import org.example.cuican.Service.admin.OrderLogService;
import org.example.cuican.Service.user.OrderService;
import org.example.cuican.utils.UserContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 管理员订单管理控制器
 * 提供后台订单管理功能，包括订单列表查询、发货、取消订单、确认收货、查看操作日志
 * 需要管理员权限（角色等级 <= 2）
 */
@RestController
@RequestMapping("/admin/order")
public class AdminOrderController {

    @Resource
    private OrderService orderService; // 订单业务服务，处理订单相关业务逻辑

    @Resource
    private OrderLogService orderLogService; // 订单日志服务，查询订单操作日志

    /**
     * 分页查询订单列表接口
     * 接口路径：/admin/order/list
     * 请求方式：GET
     * @param status 订单状态筛选，null表示查询全部订单
     * @param keyword 关键词搜索，可搜索订单号、商品名、收货人
     * @param page 当前页码，默认第1页
     * @param size 每页条数，默认10条
     * @return 统一响应结果，返回分页后的订单列表数据
     */
    @GetMapping("/list")
    public Result list(@RequestParam(required = false) Integer status,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "10") int size) {
        AdminGuard.requireAdmin(UserContext.getRoleLevel());
        return Result.success(orderService.adminList(status, keyword, page, size));
    }

    /**
     * 管理员对订单发货接口
     * 接口路径：/admin/order/ship
     * 请求方式：POST
     * @param orderId 订单ID
     * @param shipMethod 物流公司名称
     * @param trackingNo 物流单号
     * @return 统一响应结果，操作成功返回成功消息
     */
    @PostMapping("/ship")
    public Result ship(@RequestParam Long orderId,
                       @RequestParam String shipMethod,
                       @RequestParam String trackingNo) {
        Integer rl = UserContext.getRoleLevel();
        AdminGuard.requireAdmin(rl);
        Long userId = UserContext.getUserId();
        String operatorName = "admin#" + userId;
        orderService.shipByAdmin(userId, operatorName, orderId, shipMethod, trackingNo);
        return Result.success();
    }

    /**
     * 管理员强制取消订单接口
     * 接口路径：/admin/order/cancel
     * 请求方式：POST
     * @param orderId 订单ID
     * @param reason 取消原因，可选参数
     * @return 统一响应结果，操作成功返回成功消息
     */
    @PostMapping("/cancel")
    public Result cancel(@RequestParam Long orderId, @RequestParam(required = false) String reason) {
        AdminGuard.requireAdmin(UserContext.getRoleLevel());
        Long userId = UserContext.getUserId();
        orderService.cancelByAdmin(userId, "admin#" + userId, orderId, reason == null ? "" : reason);
        return Result.success();
    }

    /**
     * 管理员强制确认收货接口
     * 接口路径：/admin/order/confirm
     * 请求方式：POST
     * @param orderId 订单ID
     * @return 统一响应结果，操作成功返回成功消息
     */
    @PostMapping("/confirm")
    public Result confirm(@RequestParam Long orderId) {
        AdminGuard.requireAdmin(UserContext.getRoleLevel());
        Long userId = UserContext.getUserId();
        orderService.confirmByAdmin(userId, "admin#" + userId, orderId);
        return Result.success();
    }

    /**
     * 查询订单的所有操作日志接口
     * 接口路径：/admin/order/logs
     * 请求方式：GET
     * @param orderId 订单ID
     * @return 统一响应结果，返回该订单的所有操作日志列表
     */
    @GetMapping("/logs")
    public Result logs(@RequestParam Long orderId) {
        AdminGuard.requireAdmin(UserContext.getRoleLevel());
        return Result.success(orderLogService.listByOrderId(orderId));
    }
}