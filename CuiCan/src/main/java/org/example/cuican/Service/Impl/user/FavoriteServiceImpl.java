package org.example.cuican.Service.Impl.user;

import jakarta.annotation.Resource;
import org.example.cuican.Bean.user.Favorite;
import org.example.cuican.Mapper.user.FavoriteMapper;
import org.example.cuican.Service.user.FavoriteService;
import org.example.cuican.utils.UserContext;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户收藏业务服务实现类
 * 实现商品收藏的添加、查询、检查、删除等功能
 */
@Service
public class FavoriteServiceImpl implements FavoriteService {
    @Resource
    private FavoriteMapper favoriteMapper;

    /**
     * 查询指定用户的收藏列表
     * @param userId 用户ID
     * @return 用户收藏的商品列表
     */
    @Override
    public List<Favorite> list(Long userId) {
        return favoriteMapper.selectByUserId(userId);
    }

    /**
     * 添加商品到收藏夹
     * 自动设置当前登录用户ID和收藏时间
     * @param productId 要收藏的商品ID
     */
    @Override
    public void add(Long productId) {
        Favorite f = new Favorite();
        f.setUserId(UserContext.getUserId());
        f.setProductId(productId);
        f.setCreateTime(LocalDateTime.now());
        favoriteMapper.insert(f);
    }

    /**
     * 检查指定商品是否已被用户收藏
     * @param userId 用户ID
     * @param productId 商品ID
     * @return true表示已收藏，false表示未收藏
     */
    @Override
    public boolean check(Long userId, Long productId) {
        return favoriteMapper.countByUserIdAndProductId(userId, productId) > 0;
    }

    /**
     * 移除用户收藏的指定商品
     * 根据用户ID和商品ID删除
     * @param userId 用户ID
     * @param productId 要取消收藏的商品ID
     */
    @Override
    public void removeByProductId(Long userId, Long productId) {
        favoriteMapper.deleteByProductId(userId, productId);
    }

    /**
     * 根据收藏ID删除收藏记录
     * @param id 收藏记录ID
     */
    @Override
    public void delete(Long id) {
        favoriteMapper.delete(id);
    }
}
