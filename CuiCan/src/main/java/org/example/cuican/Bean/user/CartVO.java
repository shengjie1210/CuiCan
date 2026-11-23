package org.example.cuican.Bean.user;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 购物车视图对象(VO)
 * 用于向前端返回购物车列表，组合了购物车项信息和商品详细信息
 */
@Data
public class CartVO {
    /**
     * 购物车项ID
     */
    private Long id;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品封面图URL
     */
    private String productImage;

    /**
     * 商品单价
     */
    private BigDecimal price;

    /**
     * 购物车中该商品的数量
     */
    private Integer quantity;

    /**
     * 是否选中：1-选中，0-未选中
     */
    private Integer checked;

    /**
     * 商品剩余库存，前端用于判断是否可以购买，库存不足提示用户
     */
    private Integer stock;
}
