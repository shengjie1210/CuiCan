package org.example.cuican.Service.product;

import org.example.cuican.Bean.product.Product;
import org.example.cuican.Bean.product.ProductImage;

import java.util.List;
import java.util.Map;

/**
 * 商品服务接口
 * 提供商品查询、图片查询、分页查询等功能
 */
public interface ProductService {
    /**
     * 根据商品ID获取商品详情
     * @param id 商品ID
     * @return 商品实体
     */
    Product getById(Integer id);

    /**
     * 获取指定商品的所有图片列表
     * @param productId 商品ID
     * @return 商品图片列表
     */
    List<ProductImage> getImages(Integer productId);

    /**
     * 根据分类ID获取商品列表，按指定字段排序
     * @param categoryId 分类ID
     * @param sortField 排序字段
     * @param order 排序方式：asc/desc
     * @return 排序后的商品列表
     */
    List<Product> getByCategoryIdSort(Long categoryId,
                                      String sortField,
                                      String order);

    /**
     * 商品分页查询，支持分类筛选、关键词搜索、排序
     * @param categoryId 分类ID筛选，null表示不筛选
     * @param sortField 排序字段
     * @param order 排序方式：asc/desc
     * @param keyword 关键词搜索，null表示不搜索
     * @param page 当前页码
     * @param size 每页条数
     * @return 分页结果，包含list、total等信息
     */
    Map<String, Object> listPage(Long categoryId,
                                 String sortField,
                                 String order,
                                 String keyword,
                                 int page,
                                 int size);

    /**
     * 获取所有上架商品列表
     * @return 所有上架商品列表
     */
    List<Product> listAllOnShelf();

    /**
     * 根据条件搜索商品：分类筛选、预算限制、品类关键词筛选
     * @param categoryIds 分类ID列表，空列表表示不筛选
     * @param maxPrice 最高预算限制，null表示不限制
     * @param typeWords 品类关键词列表，空列表表示不筛选
     * @return 匹配条件的商品列表
     */
    List<Product> searchByConditions(List<Long> categoryIds, Integer maxPrice, List<String> typeWords);
}
