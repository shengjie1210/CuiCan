package org.example.cuican.Mapper.user;
/**
 * UserInfoMapper 类
 */

import org.apache.ibatis.annotations.Mapper;
import org.example.cuican.Bean.user.UserInfo;

@Mapper
public interface UserInfoMapper {
    UserInfo selectByUserId(Long userId);
    int upsert(UserInfo info);
}