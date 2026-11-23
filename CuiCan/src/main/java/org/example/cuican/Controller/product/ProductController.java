package org.example.cuican.Controller.product;

import jakarta.annotation.Resource;
import org.example.cuican.Bean.product.Product;
import org.example.cuican.Bean.product.ProductImage;
import org.example.cuican.Bean.Result;
import org.example.cuican.Service.product.ProductService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品控制器类
 * 处理商品模块的HTTP接口请求，提供商品列表查询和商品详情查询功能
 */
@RestController
@RequestMapping("/api/product")
@CrossOrigin
public class ProductController {
    @Resource
    private ProductService productService; // 商品业务服务，处理商品相关的业务逻辑

    /**
     * 商品列表分页查询接口
     * 接口路径：/api/product/list
     * 请求方式：GET
     * @param categoryId 商品分类ID，可选，按分类筛选商品
     * @param sortField 排序字段，可选，指定按哪个字段排序
     * @param order 排序方式，可选，asc表示升序，desc表示降序
     * @param keyword 搜索关键词，可选，按关键词搜索商品
     * @param page 当前页码，默认值为1，指定返回第几页数据
     * @param size 每页条数，默认值为12，指定每页返回多少商品
     * @return 统一响应结果，成功返回分页后的商品列表数据
     */
    @GetMapping("/list")
    public Result list(Long categoryId,
                       String sortField,
                       String order,
                       String keyword,
                       @RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "12") int size) {
        return Result.success(
                productService.listPage(categoryId, sortField, order, keyword, page, size)
        );
    }

    /**
     * 商品详情查询接口
     * 接口路径：/api/product/detail
     * 请求方式：GET
     * @param id 商品ID，必填，指定要查询哪个商品的详情
     * @return 统一响应结果，成功返回商品详情和商品图片列表
     */
    @GetMapping("/detail")
    public Result detail(Integer id) {
        Product product = productService.getById(id);
        List<ProductImage> images = productService.getImages(id);
        Map<String, Object> map = new HashMap<>();
        map.put("product", product);
        map.put("images", images);
        return Result.success(map);
    }
}