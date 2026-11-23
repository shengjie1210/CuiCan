package org.example.cuican.Mapper.user;
/**
 * OrderItemMapper 类
 */

import org.apache.ibatis.annotations.Mapper;
import org.example.cuican.Bean.user.OrderItem;

import java.util.List;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderItemMapper {

    List<OrderItem> selectByOrderId(Long orderId);

    int insert(OrderItem orderItem);

    int insertBatch(List<OrderItem> items);

    int deleteByOrderId(@Param("orderId") Long orderId);
}