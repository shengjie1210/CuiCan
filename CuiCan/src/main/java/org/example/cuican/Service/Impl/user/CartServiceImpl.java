package org.example.cuican.Service.Impl.user;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.cuican.Bean.user.Cart;
import org.example.cuican.Bean.user.CartVO;
import org.example.cuican.Mapper.user.CartMapper;
import org.example.cuican.Service.user.CartService;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 购物车业务服务实现类
 * 实现购物车商品查询、添加、数量修改、选中切换、删除等功能
 */
@Slf4j
@Service
public class CartServiceImpl implements CartService {
    @Resource
    private CartMapper cartMapper;

    /**
     * 查询指定用户的购物车列表，包含商品详细信息
     * @param userId 用户ID
     * @return 购物车VO列表，包含商品信息和购物车数据
     */
    @Override
    public List<CartVO> list(Long userId) {
        return cartMapper.selectCartByUserId(userId);
    }

    /**
     * 添加商品到购物车
     * 如果商品已在购物车中，则增加数量；否则新增购物车项，默认选中
     * @param userId 用户ID
     * @param productId 要添加的商品ID
     * @param quantity 添加的数量
     */
    @Override
    public void add(Long userId, Long productId, Integer quantity) {
        Cart exist = cartMapper.selectByUserIdAndProductId(userId, productId);
        if (exist != null) {
            cartMapper.updateQuantity(exist.getId(), exist.getQuantity() + quantity);
            log.info("购物车已有，增加数量: cartId={}, newQty={}", exist.getId(), exist.getQuantity() + quantity);
        } else {
            Long maxId = cartMapper.selectMaxId();
            Long newId = (maxId == null ? 0L : maxId) + 1;
            Cart cart = new Cart();
            cart.setId(newId);
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(quantity);
            cart.setChecked(1);
            cartMapper.insert(cart);
            log.info("购物车新增: cartId={}", newId);
        }
    }

    /**
     * 更新购物车商品数量
     * @param cartId 购物车项ID
     * @param quantity 新的数量
     */
    @Override
    public void updateQuantity(Long cartId, Integer quantity) {
        cartMapper.updateQuantity(cartId, quantity);
    }

    /**
     * 切换购物车商品选中状态
     * 选中变为未选中，未选中变为选中
     * @param cartId 购物车项ID
     */
    @Override
    public void toggleCheck(Long cartId) {
        cartMapper.toggleCheck(cartId);
    }

    /**
     * 删除购物车中指定商品
     * @param cartId 购物车项ID
     */
    @Override
    public void delete(Long cartId) {
        cartMapper.deleteById(cartId);
    }
}
