package org.example.cuican.Mapper.user;
/**
 * CartMapper 类
 */

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.cuican.Bean.user.Cart;
import org.example.cuican.Bean.user.CartVO;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CartMapper {
    List<CartVO> selectCartByUserId(Long userId);
    Cart selectByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
    int insert(Cart cart);
    int updateQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);
    int toggleCheck(@Param("id") Long id);
    int deleteById(Long id);
    Long selectMaxId();
    List<CartVO> selectCheckedByUserId(Long userId);
    List<CartVO> selectByIds(@Param("ids") List<Long> ids);
    int deleteByIds(@Param("ids") List<Long> ids);
}