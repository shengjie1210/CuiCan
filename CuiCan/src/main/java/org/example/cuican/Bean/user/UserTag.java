package org.example.cuican.Bean.user;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户-标签关联实体类
 * 用户和标签多对多关系关联表，记录用户拥有哪些标签
 */
@Data
public class UserTag {
    /**
     * 关联ID，主键
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 标签ID
     */
    private Long tagId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
