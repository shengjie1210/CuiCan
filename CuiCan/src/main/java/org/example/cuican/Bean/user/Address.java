package org.example.cuican.Bean.user;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户收货地址实体类
 * 存储用户的收货地址信息，每个用户可以有多个地址，支持设置默认地址
 */
@Data
public class Address {
    /**
     * 地址ID，主键自增
     */
    private Long id;

    /**
     * 所属用户ID，关联对应用户
     */
    private Long userId;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人联系电话
     */
    private String receiverPhone;

    /**
     * 省份名称
     */
    private String province;

    /**
     * 城市名称
     */
    private String city;

    /**
     * 详细地址，包括街道、门牌号等
     */
    private String detailAddress;

    /**
     * 是否默认地址：1-默认地址，0-非默认地址
     */
    private Integer isDefault;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
