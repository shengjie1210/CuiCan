package org.example.cuican.Bean.user;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * 存储商城用户的基本信息、账户信息、会员信息等核心数据
 */
@Data
public class User {
    /**
     * 用户ID，主键
     */
    private Long id;

    /**
     * 用户名，用于登录，唯一标识
     */
    private String username;

    /**
     * 密码（BCrypt加密，仅用于登录校验）
     */
    private String password;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 性别：男 / 女 / 未知
     */
    private String gender;

    /**
     * 会员等级，不同等级对应不同折扣
     */
    private Integer memberLevel;

    /**
     * 累计消费金额，用于计算会员等级
     */
    private Double totalSpent;

    /**
     * 账户余额（用于余额支付）
     */
    private Double balance;

    /**
     * 会员卡号
     */
    private String memberCardNo;

    /**
     * 账号状态：1-正常，0-已封禁
     */
    private Integer status;

    /**
     * 账号创建时间
     */
    private LocalDateTime createTime;

    /**
     * 最后更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 角色ID，用于权限系统关联
     */
    private Long roleId;

    /**
     * 用户头像URL
     */
    private String avatar;
}
