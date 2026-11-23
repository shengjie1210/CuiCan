package org.example.cuican.Service.Impl.user;

import jakarta.annotation.Resource;
import org.example.cuican.Bean.user.Address;
import org.example.cuican.Mapper.user.AddressMapper;
import org.example.cuican.Service.user.AddressService;
import org.example.cuican.utils.UserContext;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户收货地址业务服务实现类
 * 实现收货地址的增删改查和默认地址处理
 */
@Service
public class AddressServiceImpl implements AddressService {
    @Resource
    private AddressMapper addressMapper;

    /**
     * 查询指定用户的所有收货地址列表
     * @param userId 用户ID
     * @return 用户的收货地址列表
     */
    @Override
    public List<Address> list(Long userId) {
        return addressMapper.selectByUserId(userId);
    }

    /**
     * 添加用户收货地址
     * 自动设置用户ID和创建时间，如果设置为默认地址会先清除原有默认地址
     * @param address 收货地址实体，包含地址详细信息
     */
    @Override
    public void add(Address address) {
        address.setUserId(UserContext.getUserId());
        address.setCreateTime(LocalDateTime.now());
        if (Integer.valueOf(1).equals(address.getIsDefault())) {
            addressMapper.clearDefault(address.getUserId());
        }
        addressMapper.insert(address);
    }

    /**
     * 更新用户收货地址
     * 如果设置为默认地址会先清除该用户原有默认地址
     * @param address 更新的收货地址实体，包含地址ID和新信息
     */
    @Override
    public void update(Address address) {
        if (Integer.valueOf(1).equals(address.getIsDefault())) {
            addressMapper.clearDefault(UserContext.getUserId());
        }
        addressMapper.update(address);
    }

    /**
     * 删除指定收货地址
     * @param id 收货地址ID
     */
    @Override
    public void delete(Long id) {
        addressMapper.delete(id);
    }
}
