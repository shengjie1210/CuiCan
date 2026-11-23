package org.example.cuican.Service.user;

import org.example.cuican.Bean.user.CreateOrderDTO;
import org.example.cuican.Bean.user.Order;

import java.util.List;
import java.util.Map;

/**
 * 订单服务接口
 * 提供用户端订单功能和管理员后台订单管理功能
 */
public interface OrderService {
    /**
     * 获取指定用户的订单列表
     * @param userId 用户ID
     * @return 用户的所有订单列表
     */
    List<Order> list(Long userId);

    /**
     * 获取订单详情，包含订单项列表
     * @param orderId 订单ID
     * @return 订单详情，包含订单项
     */
    Map<String, Object> detail(Long orderId);

    /**
     * 创建订单
     * 根据购物车勾选商品创建订单，扣减库存
     * @param userId 用户ID
     * @param dto 创建订单DTO，包含收货地址ID和购物车项ID列表
     * @return 新创建的订单ID
     */
    Long createOrder(Long userId, CreateOrderDTO dto);

    /**
     * 用户取消订单
     * 只有未支付订单可以取消，取消后释放库存
     * @param userId 用户ID
     * @param orderId 订单ID
     */
    void cancelOrder(Long userId, Long orderId);

    /**
     * 用户确认收货
     * 已发货订单可以确认收货
     * @param userId 用户ID
     * @param orderId 订单ID
     */
    void confirmOrder(Long userId, Long orderId);

    /**
     * 用户支付订单
     * 将订单状态从待支付变更为已支付
     * @param userId 用户ID
     * @param orderId 订单ID
     * @param method 支付方式：alipay/wechat/card/balance，空默认alipay
     */
    void payOrder(Long userId, Long orderId, String method);

    /**
     * 管理员发货
     * 将订单状态从已支付变更为已发货，记录物流信息
     * @param operatorId 操作人ID
     * @param operatorName 操作人名称
     * @param orderId 订单ID
     * @param shipMethod 物流公司名称
     * @param trackingNo 物流单号
     */
    void shipByAdmin(Long operatorId, String operatorName, Long orderId, String shipMethod, String trackingNo);

    /**
     * 管理员强制取消订单
     * 任何状态订单都可以取消，取消后释放库存，记录操作日志
     * @param operatorId 操作人ID
     * @param operatorName 操作人名称
     * @param orderId 订单ID
     * @param reason 取消原因
     */
    void cancelByAdmin(Long operatorId, String operatorName, Long orderId, String reason);

    /**
     * 管理员强制确认收货
     * 代用户确认收货
     * @param operatorId 操作人ID
     * @param operatorName 操作人名称
     * @param orderId 订单ID
     */
    void confirmByAdmin(Long operatorId, String operatorName, Long orderId);

    /**
     * 管理员分页查询所有订单
     * @param status 订单状态筛选，null查询全部
     * @param keyword 关键词搜索，可搜索订单号、商品名、收货人
     * @param page 当前页码
     * @param size 每页条数
     * @return 分页结果，包含列表和总数
     */
    Map<String, Object> adminList(Integer status, String keyword, int page, int size);
}
