package com.hfut.cat_adoption_system.mapper;

import com.hfut.cat_adoption_system.model.Product;
import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.annotations.ConstructorArgs;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ProductMapper {

    @ConstructorArgs({
            @Arg(column = "product_id", javaType = String.class),
            @Arg(column = "product_name", javaType = String.class),
            @Arg(column = "category", javaType = String.class),
            @Arg(column = "price", javaType = java.math.BigDecimal.class),
            @Arg(column = "image_url", javaType = String.class),
            @Arg(column = "description", javaType = String.class),
            @Arg(column = "pay_url", javaType = String.class),
            @Arg(column = "stock", javaType = Integer.class),
            @Arg(column = "status", javaType = boolean.class),
            @Arg(column = "created_at", javaType = java.time.LocalDateTime.class)
    })
    @Select("""
            SELECT product_id, product_name, category, price, image_url, description, pay_url, stock, status, created_at
            FROM t_product
            WHERE status = 1
            ORDER BY created_at DESC, product_id
            """)
    List<Product> findActive();

    @ConstructorArgs({
            @Arg(column = "product_id", javaType = String.class),
            @Arg(column = "product_name", javaType = String.class),
            @Arg(column = "category", javaType = String.class),
            @Arg(column = "price", javaType = java.math.BigDecimal.class),
            @Arg(column = "image_url", javaType = String.class),
            @Arg(column = "description", javaType = String.class),
            @Arg(column = "pay_url", javaType = String.class),
            @Arg(column = "stock", javaType = Integer.class),
            @Arg(column = "status", javaType = boolean.class),
            @Arg(column = "created_at", javaType = java.time.LocalDateTime.class)
    })
    @Select("SELECT product_id, product_name, category, price, image_url, description, pay_url, stock, status, created_at FROM t_product ORDER BY created_at DESC")
    List<Product> findAll();

    @ConstructorArgs({
            @Arg(column = "product_id", javaType = String.class),
            @Arg(column = "product_name", javaType = String.class),
            @Arg(column = "category", javaType = String.class),
            @Arg(column = "price", javaType = java.math.BigDecimal.class),
            @Arg(column = "image_url", javaType = String.class),
            @Arg(column = "description", javaType = String.class),
            @Arg(column = "pay_url", javaType = String.class),
            @Arg(column = "stock", javaType = Integer.class),
            @Arg(column = "status", javaType = boolean.class),
            @Arg(column = "created_at", javaType = java.time.LocalDateTime.class)
    })
    @Select("SELECT product_id, product_name, category, price, image_url, description, pay_url, stock, status, created_at FROM t_product WHERE product_id = #{productId}")
    Product findById(String productId);

    @Insert("""
            INSERT INTO t_product (product_id, product_name, category, price, image_url, description, pay_url, stock, status, created_at)
            VALUES (#{productId}, #{productName}, #{category}, #{price}, #{imageUrl}, #{description}, #{payUrl}, #{stock}, #{status}, #{createdAt})
            """)
    void insert(Product product);

    @Update("""
            UPDATE t_product SET product_name = #{productName}, category = #{category}, price = #{price},
                image_url = #{imageUrl}, description = #{description}, pay_url = #{payUrl},
                stock = #{stock}, status = #{status}
            WHERE product_id = #{productId}
            """)
    int update(Product product);

    @Update("UPDATE t_product SET status = #{status} WHERE product_id = #{productId}")
    int updateStatus(@Param("productId") String productId, @Param("status") boolean status);

    @Update("UPDATE t_product SET stock = stock - #{quantity} WHERE product_id = #{productId} AND stock >= #{quantity}")
    int decreaseStock(@Param("productId") String productId, @Param("quantity") int quantity);

    @Delete("DELETE FROM t_product WHERE product_id = #{productId}")
    int delete(String productId);
}
