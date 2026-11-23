package org.example.cuican.Mapper.user;
/**
 * UserProfileMapper 类
 */

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.cuican.Bean.user.UserProfile;

@Mapper
public interface UserProfileMapper {

    UserProfile selectById(Long id);

    int update(UserProfile userProfile);

    int updateAvatar(@Param("id") Long id, @Param("avatar") String avatar);
}