package org.example.cuican.Controller.product;

import jakarta.annotation.Resource;
import org.example.cuican.Bean.product.Category;
import org.example.cuican.Service.product.CategoryService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分类控制器类
 * 处理商品分类相关的HTTP接口请求，提供分类列表查询、分类ID获取、子分类查询功能
 */
@RestController
@RequestMapping("/api/category")
@CrossOrigin
public class CategoryController {

    @Resource
    private CategoryService categoryService; // 分类业务服务，处理商品分类相关的业务逻辑

    /**
     * 获取分级分类列表接口
     * 接口路径：/api/category/list
     * 请求方式：GET
     * @return 分级结构的分类列表，用于前端展示分类菜单
     */
    @GetMapping("/list")
    public List<Category> list() {
        return categoryService.listLevel();
    }

    /**
     * 根据父分类ID获取所有子分类ID列表接口
     * 接口路径：/api/category/ids
     * 请求方式：GET
     * @param id 父分类ID，指定查询哪个分类的所有后代分类ID
     * @return 所有后代分类ID组成的列表
     */
    @GetMapping("/ids")
    public List<Long> getIds(Long id) {
        return categoryService.getCategoryIds(id);
    }

    /**
     * 获取指定分类的直接子分类列表接口
     * 接口路径：/api/category/children
     * 请求方式：GET
     * @param id 父分类ID，指定查询哪个分类的直接子分类
     * @return 直接子分类列表，返回分类完整信息
     */
    @GetMapping("/children")
    public List<Category> children(Long id) {
        return categoryService.getChildren(id);
    }
}