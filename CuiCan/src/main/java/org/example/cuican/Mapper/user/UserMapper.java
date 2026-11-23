package org.example.cuican.Mapper.user;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.cuican.Bean.user.User;

import java.math.BigDecimal;

@Mapper
public interface UserMapper {
    User selectById(Long id);
    java.util.List<org.example.cuican.Bean.user.User> selectAll();
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    BigDecimal sumConsumed(@Param("userId") Long userId);
    int updateMemberStats(@Param("id") Long id,
                          @Param("totalSpent") BigDecimal totalSpent,
                          @Param("memberLevel") Integer memberLevel);

    /** 余额充值：原子累加 */
    int addBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);
}