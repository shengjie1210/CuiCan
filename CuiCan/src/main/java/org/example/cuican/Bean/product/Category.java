package org.example.cuican.Bean.product;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品分类实体类
 * 支持多级分类，通过parentId关联父分类，实现分级分类展示
 */
@Data
public class Category {
    /**
     * 分类ID，主键自增
     */
    private Long id;

    /**
     * 父分类ID，0表示一级分类，非0表示子分类
     */
    private Long parentId;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 排序权重，值越小排序越靠前
     */
    private Integer sortOrder;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
