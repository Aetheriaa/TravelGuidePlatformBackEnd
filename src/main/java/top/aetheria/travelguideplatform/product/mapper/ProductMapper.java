package top.aetheria.travelguideplatform.product.mapper;

import org.apache.ibatis.annotations.*;
import top.aetheria.travelguideplatform.product.dto.ProductListDTO;
import top.aetheria.travelguideplatform.product.entity.Product;

import java.util.List;

@Mapper
public interface ProductMapper {

    @Insert("INSERT INTO products (type, name, description, price, supplier, stock, image, create_time, status, user_iD) " +
            "VALUES (#{type}, #{name}, #{description}, #{price}, #{supplier}, #{stock}, #{image}, #{createTime}, #{status}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Product product);

    @Select("SELECT * FROM products WHERE id = #{id}")
    Product findById(Long id);

    @Update("<script>" +
            "UPDATE products " +
            "<set>" +
            "  <if test='type != null'>type = #{type},</if>" +
            "  <if test='name != null'>name = #{name},</if>" +
            "  <if test='description != null'>description = #{description},</if>" +
            "  <if test='price != null'>price = #{price},</if>" +
            "  <if test='supplier != null'>supplier = #{supplier},</if>" +
            "  <if test='stock != null'>stock = #{stock},</if>" +
            "  <if test='image != null'>image = #{image},</if>" +
            "  <if test='status != null'>status = #{status},</if>" +
            "</set>" +
            "WHERE id = #{id}" +
            "</script>")
    void update(Product product);

    @Delete("DELETE FROM products WHERE id = #{id}")
    void delete(Long id);

    // 列表查询（支持分页、关键词搜索、类型筛选、排序）
    List<Product> list(ProductListDTO productListDTO);

    Long count(ProductListDTO productListDTO);
}