package org.example.cuican.Mapper.product;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.cuican.Bean.product.Category;
import java.util.List;

/**
 * 分类数据访问接口
 * 提供商品分类相关的数据库操作
 */
@Mapper
public interface CategoryMapper {
    /**
     * 查询所有一级分类
     * @return 一级分类列表
     */
    List<Category> selectLevel();

    /**
     * 根据父分类ID查询子分类列表
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<Category> selectByParentId(Long parentId);

    /**
     * 根据父分类ID查询直接子分类（别名，同selectByParentId）
     * @param parentId 父分类ID
     * @return 直接子分类列表
     */
    List<Category> selectChildren(Long parentId);

    /**
     * 获取指定分类下所有后代分类ID列表
     * @param id 根分类ID
     * @return 所有后代分类ID列表
     */
    List<Long> getAllCategoryIds(Long id);

    /**
     * 根据分类名称模糊查询分类列表
     * @param name 关键词，查询名称包含该关键词的分类
     * @return 匹配的分类列表
     */
    List<Category> selectByNameContaining(@Param("name") String name);
}
