package org.example.cuican.Bean.user;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单项实体类
 * 一个订单包含多个订单项，每个订单项对应一件商品，记录商品的购买信息
 */
@Data
public class OrderItem {
    /**
     * 订单项ID，主键
     */
    private Long id;

    /**
     * 所属订单ID
     */
    private Long orderId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品名称（快照，防止商品信息变更）
     */
    private String productName;

    /**
     * 商品图片（快照）
     */
    private String productImage;

    /**
     * 商品购买时的单价（快照）
     */
    private BigDecimal price;

    /**
     * 购买数量
     */
    private Integer quantity;
}
