package org.example.cuican.Bean.user;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 * 存储订单的完整信息，包括订单状态、收货信息、金额、物流信息等
 */
@Data
public class Order {
    /**
     * 订单ID，主键自增
     */
    private Long id;

    /**
     * 订单编号，唯一标识一个订单
     */
    private String orderNo;

    /**
     * 下单用户ID
     */
    private Long userId;

    /**
     * 订单总金额，用户实际支付的金额
     */
    private BigDecimal totalAmount;

    /**
     * 订单原价总额，优惠前的价格
     */
    private BigDecimal originalAmount;

    /**
     * 订单状态：
     * 0 - 待付款
     * 1 - 已付款待发货
     * 2 - 已发货待收货
     * 3 - 已完成（确认收货）
     * 4 - 已取消
     */
    private Integer status;

    /**
     * 使用的收货地址ID
     */
    private Long addressId;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人电话
     */
    private String receiverPhone;

    /**
     * 收货省份
     */
    private String province;

    /**
     * 收货城市
     */
    private String city;

    /**
     * 详细收货地址
     */
    private String detailAddress;

    /**
     * 订单备注
     */
    private String note;

    /**
     * 支付方式：如 "wechat"、"alipay" 等
     */
    private String payMethod;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 发货时间
     */
    private LocalDateTime shipTime;

    /**
     * 确认收货时间
     */
    private LocalDateTime confirmTime;

    /**
     * 物流公司名称：顺丰/圆通/中通 等
     */
    private String shipMethod;

    /**
     * 物流单号
     */
    private String trackingNo;

    /**
     * 订单创建时间
     */
    private LocalDateTime createTime;
}
