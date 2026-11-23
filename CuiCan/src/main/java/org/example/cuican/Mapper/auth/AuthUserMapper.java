package org.example.cuican.Mapper.auth;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.cuican.Bean.auth.AuthUser;
import org.example.cuican.Bean.user.User;

import java.math.BigDecimal;

@Mapper
public interface AuthUserMapper {

    AuthUser selectByUsername(@Param("username") String username);

    AuthUser selectById(@Param("id") Long id);

    void updatePassword(@Param("userId") Long userId,
                        @Param("password") String password);

    Long selectMaxId();

    void insert(User user);

    /** 余额充值：原子累加 */
    int addBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    /** 查询当前余额 */
    BigDecimal selectBalance(@Param("userId") Long userId);
}