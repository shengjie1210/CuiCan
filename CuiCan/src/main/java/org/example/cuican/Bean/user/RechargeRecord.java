package org.example.cuican.Bean.user;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户充值记录实体类
 * 记录用户的充值历史，保存每次充值的金额和时间信息
 */
@Data
public class RechargeRecord {
    /**
     * 充值记录ID，主键
     */
    private Long id;

    /**
     * 充值用户ID，关联用户表
     */
    private Long userId;

    /**
     * 充值金额
     */
    private BigDecimal amount;

    /**
     * 充值时间
     */
    private LocalDateTime createTime;
}
