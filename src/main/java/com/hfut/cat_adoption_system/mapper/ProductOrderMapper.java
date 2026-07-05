package com.hfut.cat_adoption_system.mapper;

import com.hfut.cat_adoption_system.model.ProductOrder;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductOrderMapper {
    @ConstructorArgs({
            @Arg(column = "order_id", javaType = String.class),
            @Arg(column = "user_id", javaType = String.class),
            @Arg(column = "user_name", javaType = String.class),
            @Arg(column = "product_id", javaType = String.class),
            @Arg(column = "product_name", javaType = String.class),
            @Arg(column = "quantity", javaType = Integer.class),
            @Arg(column = "amount", javaType = java.math.BigDecimal.class),
            @Arg(column = "pay_url", javaType = String.class),
            @Arg(column = "order_status", javaType = String.class),
            @Arg(column = "created_at", javaType = java.time.LocalDateTime.class)
    })
    @Select("""
            <script>
            SELECT o.order_id, o.user_id, u.user_name, o.product_id, p.product_name,
                   o.quantity, o.total_amount AS amount, o.pay_url, o.order_status, o.created_at
            FROM t_product_order o
            JOIN t_user u ON u.user_id = o.user_id
            JOIN t_product p ON p.product_id = o.product_id
            WHERE 1 = 1
            <if test="userId != null and userId != ''">AND o.user_id = #{userId}</if>
            ORDER BY o.created_at DESC
            </script>
            """)
    List<ProductOrder> findAll(@Param("userId") String userId);

    @Insert("""
            INSERT INTO t_product_order (order_id, user_id, product_id, quantity, unit_price, total_amount, pay_url, order_status, created_at)
            SELECT #{orderId}, #{userId}, #{productId}, #{quantity}, p.price, #{amount}, #{payUrl}, #{orderStatus}, #{createdAt}
            FROM t_product p
            WHERE p.product_id = #{productId}
            """)
    void insert(ProductOrder order);

    @Update("UPDATE t_product_order SET order_status = #{status} WHERE order_id = #{orderId}")
    int updateStatus(@Param("orderId") String orderId, @Param("status") String status);
}
