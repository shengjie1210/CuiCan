package org.example.cuican.Mapper.user;
/**
 * FavoriteMapper 类
 */

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.cuican.Bean.user.Favorite;

import java.util.List;

@Mapper
public interface FavoriteMapper {
    List<Favorite> selectByUserId(Long userId);
    int insert(Favorite favorite);
    int delete(Long id);

    int countByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    int deleteByProductId(@Param("userId") Long userId, @Param("productId") Long productId);
}