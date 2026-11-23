package org.example.cuican.Service.user;

import org.example.cuican.Bean.user.Address;

import java.util.List;

/**
 * 用户收货地址服务接口
 * 提供用户收货地址的增删改查功能
 */
public interface AddressService {
    /**
     * 获取指定用户的所有收货地址
     * @param userId 用户ID
     * @return 用户的所有收货地址列表
     */
    List<Address> list(Long userId);

    /**
     * 添加收货地址
     * @param address 收货地址实体
     */
    void add(Address address);

    /**
     * 更新收货地址
     * @param address 收货地址实体，必须包含ID
     */
    void update(Address address);

    /**
     * 删除收货地址
     * @param id 收货地址ID
     */
    void delete(Long id);
}
