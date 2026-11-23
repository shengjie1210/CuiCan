package org.example.cuican.Service.Impl.product;

import jakarta.annotation.Resource;
import org.example.cuican.Bean.product.Product;
import org.example.cuican.Bean.product.ProductImage;
import org.example.cuican.Mapper.product.ProductImageMapper;
import org.example.cuican.Mapper.product.ProductMapper;
import org.example.cuican.Service.product.CategoryService;
import org.example.cuican.Service.product.ProductService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 商品业务服务实现类
 * 实现商品查询、分页、图片查询、条件搜索等商品相关业务逻辑
 */
@Service
public class ProductServiceImpl implements ProductService {
    @Resource
    private ProductMapper productMapper;
    @Resource
    private CategoryService categoryService;
    @Resource
    private ProductImageMapper productImageMapper;

    /**
     * 根据商品ID查询商品详情
     * @param id 商品ID
     * @return 商品实体对象，不存在则返回null
     */
    @Override
    public Product getById(Integer id) {
        return productMapper.selectById(id);
    }

    /**
     * 根据商品ID查询该商品的所有图片列表
     * @param productId 商品ID
     * @return 商品图片实体列表
     */
    @Override
    public List<ProductImage> getImages(Integer productId) {
        return productImageMapper.selectByProductId(productId);
    }

    /**
     * 根据分类ID查询商品并按指定字段排序
     * 会包含该分类下所有子分类的商品
     * @param categoryId 分类ID，为null或1时查询全部商品
     * @param sortField 排序字段名称
     * @param order 排序方式：asc/desc
     * @return 排序后的商品列表
     */
    @Override
    public List<Product> getByCategoryIdSort(Long categoryId,
                                             String sortField,
                                             String order) {
        List<Long> ids = null;
        if (categoryId != null && categoryId != 1) {
            ids = categoryService.getCategoryIds(categoryId);
        }
        return productMapper.selectByCategoryIdsSort(ids, sortField, order);
    }

    /**
     * 分页查询商品列表，支持按分类筛选和关键词搜索
     * 包含该分类下所有子分类的商品，对页码和条数做边界校验
     * @param categoryId 分类ID，为null或1时不按分类筛选
     * @param sortField 排序字段名称
     * @param order 排序方式：asc/desc
     * @param keyword 商品名称关键词搜索，为null或空时不筛选
     * @param page 当前页码，最小为1
     * @param size 每页条数，范围1-100
     * @return 分页结果Map，包含list(商品列表)、total(总条数)、page(当前页)、size(每页条数)
     */
    @Override
    public Map<String, Object> listPage(Long categoryId,
                                        String sortField,
                                        String order,
                                        String keyword,
                                        int page,
                                        int size) {
        List<Long> ids = null;
        if (categoryId != null && categoryId != 1) {
            ids = categoryService.getCategoryIds(categoryId);
        }
        if (page < 1) page = 1;
        if (size < 1) size = 12;
        if (size > 100) size = 100;
        String kw = (keyword == null || keyword.trim().isEmpty()) ? null : keyword.trim();
        int offset = (page - 1) * size;
        List<Product> list = productMapper.selectPage(ids, sortField, order, kw, offset, size);
        int total = productMapper.countPage(ids, kw);
        Map<String, Object> map = new HashMap<>();
        map.put("list", list);
        map.put("total", total);
        map.put("page", page);
        map.put("size", size);
        return map;
    }

    /**
     * 根据多条件搜索商品：分类筛选、最高预算限制、品类关键词筛选
     * @param categoryIds 筛选的分类ID列表，为空不筛选
     * @param maxPrice 最高预算限制，为null不限制
     * @param typeWords 品类关键词列表，用于商品名称模糊匹配
     * @return 匹配所有条件的商品列表，按销量倒序排列
     */
    @Override
    public List<Product> searchByConditions(List<Long> categoryIds, Integer maxPrice, List<String> typeWords) {
        return productMapper.selectByConditions(categoryIds, maxPrice, typeWords);
    }

    /**
     * 查询所有上架商品列表
     * @return 所有上架商品列表
     */
    @Override
    public List<Product> listAllOnShelf() {
        return productMapper.selectAllOnShelf();
    }
}
