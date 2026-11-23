package org.example.cuican.Mapper.user;
/**
 * AddressMapper 类
 */

import org.apache.ibatis.annotations.Mapper;
import org.example.cuican.Bean.user.Address;

import java.util.List;

@Mapper
public interface AddressMapper {

    List<Address> selectByUserId(Long userId);

    int insert(Address address);

    int update(Address address);

    int delete(Long id);

    int clearDefault(Long userId);
}