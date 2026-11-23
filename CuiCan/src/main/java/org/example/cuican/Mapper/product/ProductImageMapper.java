package org.example.cuican.Mapper.product;
/**
 * ProductImageMapper 类
 */

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.cuican.Bean.product.ProductImage;

import java.util.List;

@Mapper
public interface ProductImageMapper {
    List<ProductImage> selectByProductId(@Param("productId") Integer productId);
}