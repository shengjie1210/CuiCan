package org.example.cuican.Service.product;

import org.example.cuican.Bean.product.Category;

import java.util.List;

/**
 * 商品分类服务接口
 * 提供商品分类查询，分级结构查询，获取子分类等功能
 */
public interface CategoryService {
    /**
     * 获取分级分类列表
     * 返回树形结构的分类列表，用于前端展示分级分类菜单
     * @return 分级分类列表，每个分类包含子分类
     */
    List<Category> listLevel();

    /**
     * 获取指定分类的所有后代分类ID列表
     * 包括所有子子孙孙分类ID
     * @param id 父分类ID
     * @return 所有后代分类ID列表
     */
    List<Long> getCategoryIds(Long id);

    /**
     * 获取指定分类的直接子分类列表
     * @param id 父分类ID
     * @return 直接子分类列表，返回分类完整信息
     */
    List<Category> getChildren(Long id);
}
