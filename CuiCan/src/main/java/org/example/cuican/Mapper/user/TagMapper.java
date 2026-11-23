package org.example.cuican.Mapper.user;
/**
 * TagMapper 类
 */

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.cuican.Bean.user.Tag;

import java.util.List;

@Mapper
public interface TagMapper {

    List<String> selectTagNamesByUserId(Long userId);

    List<Tag> selectAll();

    List<Long> selectTagIdsByUserId(Long userId);

    int insertUserTag(@Param("userId") Long userId, @Param("tagId") Long tagId);

    int deleteUserTag(@Param("userId") Long userId, @Param("tagId") Long tagId);
}