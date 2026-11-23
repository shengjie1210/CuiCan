package org.example.cuican.Mapper.admin;
/**
 * OrderLogMapper 类
 */

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.cuican.Bean.admin.OrderLog;

import java.util.List;

@Mapper
public interface OrderLogMapper {

    int insert(OrderLog log);

    List<OrderLog> selectByOrderId(@Param("orderId") Long orderId);
}