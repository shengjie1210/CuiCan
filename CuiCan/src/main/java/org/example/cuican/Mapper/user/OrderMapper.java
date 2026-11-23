package org.example.cuican.Mapper.user;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.cuican.Bean.user.Order;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {

    List<Order> selectByUserId(@Param("userId") Long userId);

    Order selectById(@Param("id") Long id);

    int insert(Order order);

    Long selectMaxId();

    /** 单改 status */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /** 标记已支付（status=1 + pay_time + pay_method） */
    int updatePaidInfo(@Param("id") Long id,
                       @Param("status") Integer status,
                       @Param("payTime") LocalDateTime payTime,
                       @Param("payMethod") String payMethod);

    /** 标记已发货（status=2 + ship_time + ship_method + tracking_no） */
    int updateShipInfo(@Param("id") Long id,
                       @Param("status") Integer status,
                       @Param("shipTime") LocalDateTime shipTime,
                       @Param("shipMethod") String shipMethod,
                       @Param("trackingNo") String trackingNo);

    /** 标记已确认收货（status=3 + confirm_time） */
    int updateConfirmInfo(@Param("id") Long id,
                         @Param("status") Integer status,
                         @Param("confirmTime") LocalDateTime confirmTime);

    /** 物理删除 */
    int deleteById(@Param("id") Long id);

    /** === Admin === */

    /** 查所有订单（status / keyword 可空；offset/limit 用于分页） */
    List<Order> selectAll(@Param("status") Integer status,
                          @Param("keyword") String keyword,
                          @Param("offset") int offset,
                          @Param("limit") int limit);

    /** 同条件下总条数 */
    int countAll(@Param("status") Integer status,
                 @Param("keyword") String keyword);
}