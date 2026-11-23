package org.example.cuican.Bean.user;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户照片实体类
 * 存储用户上传的个人相册照片，支持用户展示多个个人照片
 */
@Data
public class UserPhoto {
    /**
     * 照片ID，主键
     */
    private Long id;

    /**
     * 关联用户ID，对应用户主表
     */
    private Long userId;

    /**
     * 照片访问URL地址
     */
    private String url;

    /**
     * 是否为精选封面：1-是，0-否
     */
    private Integer isFeatured;

    /**
     * 排序权重，值越小越靠前
     */
    private Integer sort;

    /**
     * 上传时间
     */
    private LocalDateTime createTime;
}
