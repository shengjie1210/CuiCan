package org.example.cuican.Bean.admin;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单操作日志实体类
 * 记录订单的所有操作历史，包括管理员操作和系统自动操作，用于订单溯源和问题排查
 */
@Data
public class OrderLog {
    /**
     * 日志ID，主键自增
     */
    private Long id;

    /**
     * 关联的订单ID
     */
    private Long orderId;

    /**
     * 操作人ID，管理员操作为管理员ID，系统操作为空或0
     */
    private Long operatorId;

    /**
     * 操作人姓名，用于展示操作人信息
     */
    private String operatorName;

    /**
     * 操作类型：
     * ship - 发货
     * cancel - 取消订单
     * confirm - 确认收货
     * auto_cancel - 系统自动取消超时未支付订单
     * auto_confirm - 系统自动确认收货
     */
    private String action;

    /**
     * 操作备注信息，比如取消原因等
     */
    private String remark;

    /**
     * 操作创建时间
     */
    private LocalDateTime createdAt;
}
