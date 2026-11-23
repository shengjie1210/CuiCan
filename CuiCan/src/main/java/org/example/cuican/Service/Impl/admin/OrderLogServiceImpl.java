package org.example.cuican.Service.Impl.admin;

import jakarta.annotation.Resource;
import org.example.cuican.Bean.admin.OrderLog;
import org.example.cuican.Mapper.admin.OrderLogMapper;
import org.example.cuican.Service.admin.OrderLogService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单操作日志服务实现类
 * 实现订单操作日志的记录和查询功能
 */
@Service
public class OrderLogServiceImpl implements OrderLogService {

    @Resource
    private OrderLogMapper orderLogMapper;

    /**
     * 记录订单操作日志
     * @param orderId 订单ID
     * @param operatorId 操作人ID
     * @param operatorName 操作人名称
     * @param action 操作类型
     * @param remark 操作备注
     */
    @Override
    public void log(Long orderId, Long operatorId, String operatorName, String action, String remark) {
        OrderLog log = new OrderLog();
        log.setOrderId(orderId);
        log.setOperatorId(operatorId);
        log.setOperatorName(operatorName);
        log.setAction(action);
        log.setRemark(remark);
        orderLogMapper.insert(log);
    }

    /**
     * 查询指定订单的所有操作日志
     * @param orderId 订单ID
     * @return 订单操作日志列表
     */
    @Override
    public List<OrderLog> listByOrderId(Long orderId) {
        return orderLogMapper.selectByOrderId(orderId);
    }
}
