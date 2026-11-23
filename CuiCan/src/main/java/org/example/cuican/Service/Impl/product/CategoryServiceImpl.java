package org.example.cuican.Service.Impl.product;

import jakarta.annotation.Resource;
import org.example.cuican.Bean.product.Category;
import org.example.cuican.Mapper.product.CategoryMapper;
import org.example.cuican.Service.product.CategoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品分类服务实现类
 * 实现商品分类查询，分级结构查询，获取子分类等功能
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    /**
     * 获取分级分类列表
     * 返回树形结构的分类列表，每个分类包含子分类
     * @return 分级分类列表
     */
    @Override
    public List<Category> listLevel() {
        return categoryMapper.selectLevel();
    }

    /**
     * 获取指定分类的所有后代分类ID列表
     * 递归收集所有子子孙孙分类ID
     * @param id 父分类ID
     * @return 所有后代分类ID列表，包含当前分类ID
     */
    @Override
    public List<Long> getCategoryIds(Long id) {
        List<Long> result = new ArrayList<>();
        collect(id, result);
        return result;
    }

    /**
     * 递归收集所有后代分类ID
     * @param id 当前分类ID
     * @param result 结果收集列表
     */
    private void collect(Long id, List<Long> result) {
        result.add(id);
        List<Category> children = categoryMapper.selectByParentId(id);
        if (children != null && !children.isEmpty()) {
            for (Category c : children) {
                collect(c.getId(), result);
            }
        }
    }

    /**
     * 获取指定分类的直接子分类列表
     * @param id 父分类ID
     * @return 直接子分类列表
     */
    @Override
    public List<Category> getChildren(Long id) {
        return categoryMapper.selectByParentId(id);
    }
}
