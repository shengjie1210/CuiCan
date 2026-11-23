package org.example.cuican.Service.Impl.user;

import jakarta.annotation.Resource;
import org.example.cuican.Mapper.user.UserMapper;
import org.example.cuican.Service.admin.SystemSettingService;
import org.example.cuican.Service.user.MemberLevelService;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

/**
 * 会员等级业务服务实现类
 * 根据用户累计消费金额计算和更新会员等级
 */
@Service
public class MemberLevelServiceImpl implements MemberLevelService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private SystemSettingService settingService;

    /**
     * 刷新指定用户的会员等级统计信息
     * 重新计算累计消费和会员等级并更新到数据库
     * @param userId 用户ID，为null则不处理
     */
    @Override
    public void refresh(Long userId) {
        if (userId == null) return;
        BigDecimal spent = userMapper.sumConsumed(userId);
        userMapper.updateMemberStats(userId, spent, computeLevel(spent));
    }

    /**
     * 根据累计消费金额计算会员等级
     * 等级阈值从系统配置读取，默认阈值：V2≥5000，V3≥20000，V4≥100000，V5≥500000
     * @param totalSpent 用户累计消费金额
     * @return 会员等级，1-5级，默认返回1级
     */
    @Override
    public int computeLevel(BigDecimal totalSpent) {
        BigDecimal spent = totalSpent == null ? BigDecimal.ZERO : totalSpent;
        int v5 = settingService.getInt("member.level.v5", 500000);
        int v4 = settingService.getInt("member.level.v4", 100000);
        int v3 = settingService.getInt("member.level.v3", 20000);
        int v2 = settingService.getInt("member.level.v2", 5000);
        if (spent.compareTo(BigDecimal.valueOf(v5)) >= 0) return 5;
        if (spent.compareTo(BigDecimal.valueOf(v4)) >= 0) return 4;
        if (spent.compareTo(BigDecimal.valueOf(v3)) >= 0) return 3;
        if (spent.compareTo(BigDecimal.valueOf(v2)) >= 0) return 2;
        return 1;
    }
}
