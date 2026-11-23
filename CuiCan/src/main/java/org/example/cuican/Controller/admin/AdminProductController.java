package org.example.cuican.Controller.admin;

import jakarta.annotation.Resource;
import org.example.cuican.Bean.Result;
import org.example.cuican.Bean.product.Product;
import org.example.cuican.Bean.product.ProductImage;
import org.example.cuican.Mapper.product.ProductMapper;
import org.example.cuican.Mapper.admin.AdminProductMapper;
import org.example.cuican.Service.product.ProductService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 管理员商品管理控制器
 * 提供商品的增删改查功能，包括列表查询、详情、保存、删除、库存修改
 */
@RestController
@RequestMapping("/admin/product")
public class AdminProductController {

    @Resource
    private ProductService productService; // 商品业务服务，处理商品相关业务逻辑

    @Resource
    private AdminProductMapper adminProductMapper; // 管理员商品Mapper，数据库访问

    /**
     * 分页查询商品列表接口
     * 接口路径：/admin/product/list
     * 请求方式：GET
     * @param keyword 商品名称关键词搜索，可选参数
     * @param categoryId 分类ID筛选，可选参数
     * @param page 当前页码，默认第1页
     * @param size 每页条数，默认10条
     * @return 统一响应结果，返回包含商品列表和总条数的分页结果
     */
    @GetMapping("/list")
    public Result list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) Long categoryId,
                       @RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "10") int size) {
        if (page < 1) page = 1;
        int offset = (page - 1) * size;
        int total;
        List<Product> rows;
        if (keyword != null && !keyword.isEmpty()) {
            total = adminProductMapper.countByKeyword(keyword);
            rows = adminProductMapper.searchByNamePage(keyword, offset, size);
        } else if (categoryId != null) {
            total = adminProductMapper.countByCategory(categoryId);
            rows = adminProductMapper.selectByCategoryPage(categoryId, offset, size);
        } else {
            total = adminProductMapper.countAll();
            rows = adminProductMapper.selectAllPage(offset, size);
        }
        return Result.success(java.util.Map.of("list", rows, "total", total));
    }

    /**
     * 获取商品详情接口
     * 接口路径：/admin/product/detail
     * 请求方式：GET
     * @param id 商品ID
     * @return 统一响应结果，返回商品详情和图片列表
     */
    @GetMapping("/detail")
    public Result detail(Integer id) {
        Product p = adminProductMapper.selectById(id);
        List<ProductImage> imgs = productService.getImages(id);
        return Result.success(java.util.Map.of("product", p, "images", imgs));
    }

    /**
     * 保存商品接口
     * 接口路径：/admin/product/save
     * 请求方式：POST
     * @param product 商品信息实体
     * @return 统一响应结果，返回商品ID
     * 如果ID不存在则新增，存在则更新
     */
    @PostMapping("/save")
    public Result save(@RequestBody Product product) {
        if (product.getId() == null) {
            Long maxId = adminProductMapper.selectMaxId();
            product.setId(((Long)((maxId == null ? 2000L : maxId) + 1)).intValue());
            adminProductMapper.insert(product);
        } else {
            adminProductMapper.update(product);
        }
        return Result.success(product.getId());
    }

    /**
     * 删除商品接口
     * 接口路径：/admin/product/delete
     * 请求方式：POST
     * @param id 商品ID
     * @return 统一响应结果，删除成功返回成功消息
     */
    @PostMapping("/delete")
    public Result delete(Integer id) {
        adminProductMapper.deleteById(id);
        return Result.success();
    }

    /**
     * 修改商品库存接口
     * 接口路径：/admin/product/stock
     * 请求方式：POST
     * @param id 商品ID
     * @param stock 新的库存数量
     * @return 统一响应结果，修改成功返回成功消息
     */
    @PostMapping("/stock")
    public Result stock(Integer id, Integer stock) {
        adminProductMapper.updateStock(id, stock);
        return Result.success();
    }
}
