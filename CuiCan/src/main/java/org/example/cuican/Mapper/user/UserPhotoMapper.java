package org.example.cuican.Mapper.user;
/**
 * UserPhotoMapper 类
 */

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserPhotoMapper {
    List<String> selectPhotosByUserId(Long userId);
    int insert(@Param("userId") Long userId, @Param("url") String url);
    int deleteByUserIdAndUrl(@Param("userId") Long userId, @Param("url") String url);
}