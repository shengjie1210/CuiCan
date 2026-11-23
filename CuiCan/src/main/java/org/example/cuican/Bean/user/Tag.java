package org.example.cuican.Bean.user;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品标签实体类
 * 用于给商品打标签，如新品、热销、推荐等，实现商品分类标签功能
 */
@Data
public class Tag {
    /**
     * 标签ID，主键
     */
    private Long id;

    /**
     * 标签名称
     */
    private String name;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
