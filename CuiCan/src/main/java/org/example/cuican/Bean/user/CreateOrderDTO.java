package org.example.cuican.Bean.user;

import lombok.Data;
import java.util.List;

/**
 * 创建订单请求数据传输对象(DTO)
 * 接收前端创建订单时提交的信息，支持立即购买和购物车结算两种方式
 */
@Data
public class CreateOrderDTO {
    /**
     * 商品ID，立即购买单个商品时使用
     */
    private Long productId;

    /**
     * 商品数量，立即购买时使用
     */
    private Integer quantity;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人联系电话
     */
    private String receiverPhone;

    /**
     * 收货省份
     */
    private String province;

    /**
     * 收货城市
     */
    private String city;

    /**
     * 详细收货地址
     */
    private String detailAddress;

    /**
     * 订单备注，用户添加的备注信息
     */
    private String note;

    /**
     * 购物车中选中项ID列表，从购物车结算时使用
     */
    private List<Long> cartIds;
}
