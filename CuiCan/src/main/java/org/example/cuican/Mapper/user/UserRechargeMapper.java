package org.example.cuican.Mapper.user;
/**
 * UserRechargeMapper 类
 */

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.cuican.Bean.user.RechargeRecord;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface UserRechargeMapper {

    int insert(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    List<RechargeRecord> selectByUserId(@Param("userId") Long userId);
}