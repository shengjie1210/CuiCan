package org.example.cuican.Service.user;

import org.example.cuican.Bean.user.Favorite;

import java.util.List;

/**
 * 用户收藏服务接口
 * 提供商品收藏的查询、添加、删除、检查功能
 */
public interface FavoriteService {
    /**
     * 获取指定用户的收藏列表
     * @param userId 用户ID
     * @return 用户收藏列表
     */
    List<Favorite> list(Long userId);

    /**
     * 添加商品到收藏
     * @param productId 商品ID
     */
    void add(Long productId);

    /**
     * 删除收藏记录
     * @param id 收藏记录ID
     */
    void delete(Long id);

    /**
     * 检查商品是否已被用户收藏
     * @param userId 用户ID
     * @param productId 商品ID
     * @return true 已收藏，false 未收藏
     */
    boolean check(Long userId, Long productId);

    /**
     * 按商品ID删除收藏记录
     * @param userId 用户ID
     * @param productId 商品ID
     */
    void removeByProductId(Long userId, Long productId);
}
