package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * 商品请求DTO
 * 
 * 用于接收创建或更新商品的请求参数，通过Jakarta Validation注解实现表单校验。
 * 使用Java Record实现，自动生成getter、equals、hashCode等方法。
 */
public record ProductRequest(
                /** 商品名称（必填） */
                @NotBlank(message = "商品名称不能为空") String productName,

                /** 商品分类（必填） */
                @NotBlank(message = "商品分类不能为空") String category,

                /** 商品价格（必填，最小为0.01） */
                @NotNull(message = "商品价格不能为空") @DecimalMin(value = "0.01", message = "商品价格不能小于0.01") BigDecimal price,

                /** 商品图片URL（必填） */
                @NotBlank(message = "商品图片不能为空") String imageUrl,

                /** 商品描述（必填） */
                @NotBlank(message = "商品描述不能为空") String description,

                /** 支付链接 */
                String payUrl,

                /** 商品库存（必填，最小为0） */
                @NotNull(message = "商品库存不能为空") @Min(value = 0, message = "商品库存不能小于0") Integer stock,

                /** 商品状态（true表示上架，false表示下架） */
                boolean status) {
}