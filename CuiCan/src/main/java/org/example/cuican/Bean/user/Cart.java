package org.example.cuican.Bean.user;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 购物车实体类
 * 存储用户购物车中的商品信息，每个商品是一条购物车记录，包含商品数量和选中状态
 */
@Data
public class Cart {
    /**
     * 购物车项ID，主键自增
     */
    private Long id;

    /**
     * 所属用户ID
     */
    private Long userId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 购买数量，用户添加到购物车的商品数量
     */
    private Integer quantity;

    /**
     * 是否选中：1-选中，0-未选中，结算时只计算选中的商品
     */
    private Integer checked;

    /**
     * 添加到购物车的时间
     */
    private LocalDateTime createTime;
}
