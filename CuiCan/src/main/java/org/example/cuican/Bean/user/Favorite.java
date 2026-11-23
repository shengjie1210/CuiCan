package org.example.cuican.Bean.user;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户收藏实体类
 * 记录用户收藏的商品，一个用户可以收藏多个商品
 */
@Data
public class Favorite {
    /**
     * 收藏记录ID，主键自增
     */
    private Long id;

    /**
     * 所属用户ID
     */
    private Long userId;

    /**
     * 收藏的商品ID
     */
    private Long productId;

    /**
     * 收藏时间
     */
    private LocalDateTime createTime;
}
