package org.example.cuican.Bean.user;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户详细信息实体类
 * 存储用户的扩展信息，与User主表分离，降低主表复杂度，存放非核心用户信息
 */
@Data
public class UserInfo {
    /**
     * 信息ID，主键
     */
    private Long id;

    /**
     * 关联用户ID，对应User表主键
     */
    private Long userId;

    /**
     * 个人简介
     */
    private String bio;

    /**
     * 个性签名
     */
    private String signature;

    /**
     * 职业
     */
    private String profession;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 常住地址
     */
    private String address;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 最后更新时间
     */
    private LocalDateTime updateTime;
}
