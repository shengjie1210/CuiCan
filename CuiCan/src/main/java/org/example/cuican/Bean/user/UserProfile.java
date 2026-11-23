package org.example.cuican.Bean.user;

import lombok.Data;
import java.util.List;
import java.time.LocalDateTime;

/**
 * 用户详情VO
 * 整合用户基本信息、扩展信息、标签、照片等数据，用于用户主页展示
 */
@Data
public class UserProfile {
    /**
     * 用户ID，主键
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 性别：男 / 女 / 未知
     */
    private String gender;

    /**
     * 会员等级
     */
    private Integer memberLevel;

    /**
     * 累计消费金额
     */
    private Double totalSpent;

    /**
     * 账户余额
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
     * 角色ID，权限关联
     */
    private Long roleId;

    /**
     * 用户头像URL
     */
    private String avatar;

    /**
     * 用户详细扩展信息
     */
    private UserInfo userInfo;

    /**
     * 用户关联标签列表
     */
    private List<String> tags;

    /**
     * 用户照片URL列表
     */
    private List<String> photos;
}
