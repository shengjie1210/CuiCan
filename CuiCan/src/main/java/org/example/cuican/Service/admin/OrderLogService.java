package org.example.cuican.Service.admin;

import org.example.cuican.Bean.admin.OrderLog;

import java.util.List;

/**
 * 订单操作日志服务接口
 * 记录订单的所有操作历史，包括发货、取消、确认收货等操作，方便追溯订单变更历史
 */
public interface OrderLogService {
    /**
     * 记录订单操作日志
     * @param orderId 订单ID
     * @param operatorId 操作人ID
     * @param operatorName 操作人名称
     * @param action 操作类型
     * @param remark 操作备注
     */
    void log(Long orderId, Long operatorId, String operatorName, String action, String remark);

    /**
     * 查询指定订单的所有操作日志
     * @param orderId 订单ID
     * @return 订单操作日志列表，按操作时间排序
     */
    List<OrderLog> listByOrderId(Long orderId);
}
