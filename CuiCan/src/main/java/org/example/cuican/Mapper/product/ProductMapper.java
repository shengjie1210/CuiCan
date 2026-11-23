package org.example.cuican.Mapper.product;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.cuican.Bean.product.Product;

import java.util.List;

/**
 * 商品数据访问接口
 * 提供商品查询、库存增减、条件搜索等数据库操作
 */
@Mapper
public interface ProductMapper {
    /**
     * 根据商品ID查询商品
     * @param id 商品ID
     * @return 商品实体
     */
    Product selectById(@Param("id") Integer id);

    /**
     * 查询所有商品
     * @return 所有商品列表
     */
    List<Product> selectAll();

    /**
     * 扣减商品库存
     * @param productId 商品ID
     * @param quantity 扣减数量
     * @return 影响行数
     */
    int decrementStock(@Param("productId") Integer productId, @Param("quantity") Integer quantity);

    /**
     * 增加商品库存（取消订单时归还）
     * @param productId 商品ID
     * @param quantity 增加数量
     * @return 影响行数
     */
    int incrementStock(@Param("productId") Integer productId, @Param("quantity") Integer quantity);

    /**
     * 根据分类ID列表查询商品并按指定字段排序
     * @param ids 分类ID列表
     * @param sortField 排序字段
     * @param order 排序方式
     * @return 排序后的商品列表
     */
    List<Product> selectByCategoryIdsSort(@Param("ids") List<Long> ids,
                                          @Param("sortField") String sortField,
                                          @Param("order") String order);

    /**
     * 分页查询商品列表
     * @param ids 分类ID筛选
     * @param sortField 排序字段
     * @param order 排序方式
     * @param keyword 关键词搜索
     * @param offset 偏移量
     * @param limit 每页条数
     * @return 商品列表
     */
    List<Product> selectPage(@Param("ids") List<Long> ids,
                             @Param("sortField") String sortField,
                             @Param("order") String order,
                             @Param("keyword") String keyword,
                             @Param("offset") int offset,
                             @Param("limit") int limit);

    /**
     * 分页查询统计总数
     * @param ids 分类ID筛选
     * @param keyword 关键词搜索
     * @return 总条数
     */
    int countPage(@Param("ids") List<Long> ids,
                  @Param("keyword") String keyword);

    /**
     * 根据多条件搜索商品：分类筛选、价格上限、品类关键词筛选
     * @param ids 分类ID列表，为空不筛选
     * @param maxPrice 最高价格限制，为null不限制
     * @param typeWords 品类关键词列表，匹配商品名称
     * @return 匹配条件的商品列表
     */
    List<Product> selectByConditions(@Param("ids") List<Long> ids,
                                     @Param("maxPrice") Integer maxPrice,
                                     @Param("typeWords") List<String> typeWords);

    /**
     * 查询所有上架商品
     * @return 上架商品列表
     */
    List<Product> selectAllOnShelf();
}
