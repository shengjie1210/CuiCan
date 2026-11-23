package org.example.cuican.Mapper.admin;
/**
 * AdminProductMapper 类
 */

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.cuican.Bean.product.Product;
import java.util.List;

@Mapper
public interface AdminProductMapper {
    Product selectById(@Param("id") Integer id);
    List<Product> selectAll();
    List<Product> searchByName(@Param("keyword") String keyword);
    List<Product> selectByCategory(@Param("categoryId") Long categoryId);
    Long selectMaxId();
    int insert(Product product);
    int update(Product product);
    int deleteById(@Param("id") Integer id);
    int updateStock(@Param("id") Integer id, @Param("stock") Integer stock);
    int countAll();
    int countByCategory(@Param("categoryId") Long categoryId);
    int countByKeyword(@Param("keyword") String keyword);
    List<Product> selectAllPage(@Param("offset") int offset, @Param("limit") int limit);
    List<Product> selectByCategoryPage(@Param("categoryId") Long categoryId, @Param("offset") int offset, @Param("limit") int limit);
    List<Product> searchByNamePage(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);
}