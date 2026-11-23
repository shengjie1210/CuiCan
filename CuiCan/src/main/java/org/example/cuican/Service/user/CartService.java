package org.example.cuican.Service.user;

import org.example.cuican.Bean.user.CartVO;
import java.util.List;

/**
 * 购物车服务接口
 * 提供购物车商品查询、添加、修改数量、切换勾选、删除功能
 */
public interface CartService {
    /**
     * 获取指定用户的购物车列表
     * @param userId 用户ID
     * @return 购物车VO列表，包含商品信息和购物车记录
     */
    List<CartVO> list(Long userId);

    /**
     * 添加商品到购物车
     * 如果商品已经在购物车中，则增加数量
     * @param userId 用户ID
     * @param productId 商品ID
     * @param quantity 添加数量
     */
    void add(Long userId, Long productId, Integer quantity);

    /**
     * 修改购物车商品数量
     * @param cartId 购物车记录ID
     * @param quantity 新数量
     */
    void updateQuantity(Long cartId, Integer quantity);

    /**
     * 切换购物车商品勾选状态
     * 如果当前勾选则取消勾选，如果未勾选则勾选
     * @param cartId 购物车记录ID
     */
    void toggleCheck(Long cartId);

    /**
     * 删除购物车商品
     * @param cartId 购物车记录ID
     */
    void delete(Long cartId);
}
