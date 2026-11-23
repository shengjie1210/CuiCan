package org.example.cuican.Bean.product;

import lombok.Data;

/**
 * 商品图片实体类
 * 一个商品可以有多张详情图片，该表存储商品的所有图片信息
 */
@Data
public class ProductImage {

    /**
     * 图片ID，主键自增
     */
    private Integer id;

    /**
     * 关联商品ID
     */
    private Integer productId;

    /**
     * 图片URL地址
     */
    private String imageUrl;

    /**
     * 排序权重，值越小展示越靠前
     */
    private Integer sortOrder;
}
