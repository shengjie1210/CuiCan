package org.example.cuican.Bean.product;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品实体类
 * 存储商品的基本信息，包括价格、库存、分类、描述等
 */
@Data
public class Product {
    /**
     * 商品ID，主键
     */
    private Integer id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 所属分类ID
     */
    private Integer categoryId;

    /**
     * 商品售价（当前销售价格）
     */
    private BigDecimal price;

    /**
     * 商品原价（划线价格，用于展示原价优惠）
     */
    private BigDecimal originalPrice;

    /**
     * 库存数量
     */
    private Integer stock;

    /**
     * 商品销量
     */
    private Integer sales;

    /**
     * 商品详细描述，富文本格式
     */
    private String description;

    /**
     * 商品封面图URL
     */
    private String coverImage;

    /**
     * 商品状态：1-上架，0-下架
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最后更新时间
     */
    private Date updateTime;
}