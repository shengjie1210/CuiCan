package org.example.cuican.Service.Impl.user;

import jakarta.annotation.Resource;
import org.example.cuican.Bean.user.User;
import org.example.cuican.Bean.user.UserProfile;
import org.example.cuican.Mapper.user.UserMapper;
import org.example.cuican.Service.user.UserService;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 用户基础信息业务服务实现类
 * 实现用户基础资料查询、用户列表查询、账号状态修改等功能
 */
@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;

    /**
     * 获取用户基础资料档案，从User表组装返回UserProfile
     * 包含用户ID、用户名、手机号、性别、会员等级、累计消费、账户余额等基础信息
     * @param userId 用户ID
     * @return 用户基础资料对象，用户不存在则返回null
     */
    @Override
    public UserProfile getUserProfile(Long userId) {
        User u = userMapper.selectById(userId);
        if (u == null) return null;
        UserProfile p = new UserProfile();
        p.setId(u.getId());
        p.setUsername(u.getUsername());
        p.setPhone(u.getPhone());
        p.setGender(u.getGender());
        p.setMemberLevel(u.getMemberLevel());
        p.setTotalSpent(u.getTotalSpent());
        p.setBalance(u.getBalance());
        p.setMemberCardNo(u.getMemberCardNo());
        p.setStatus(u.getStatus());
        p.setCreateTime(u.getCreateTime());
        p.setUpdateTime(u.getUpdateTime());
        p.setRoleId(u.getRoleId());
        return p;
    }

    /**
     * 查询系统所有用户列表
     * @return 所有用户实体列表
     */
    @Override
    public List<User> listAll() {
        return userMapper.selectAll();
    }

    /**
     * 修改用户账号状态（启用/禁用）
     * @param userId 用户ID
     * @param status 新状态，0禁用，1启用
     */
    @Override
    public void setStatus(Long userId, Integer status) {
        userMapper.updateStatus(userId, status);
    }
}
