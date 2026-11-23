package org.example.cuican.Service.user;

import java.math.BigDecimal;

/**
 * 会员等级服务接口
 * 根据用户累计消费金额计算和更新会员等级，不同等级对应不同折扣
 */
public interface MemberLevelService {
    /**
     * 按累计消费金额重算并更新用户会员等级到数据库
     * @param userId 用户ID
     */
    void refresh(Long userId);

    /**
     * 根据累计消费金额计算会员等级（V1-V5）
     * 等级门槛从系统配置中读取
     * @param totalSpent 累计消费金额
     * @return 计算出的会员等级
     */
    int computeLevel(BigDecimal totalSpent);
}
