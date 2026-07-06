package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * 订单请求DTO
 * 
 * 用于接收创建订单的请求参数，通过Jakarta Validation注解实现表单校验。
 * 使用Java Record实现，自动生成getter、equals、hashCode等方法。
 */
public record OrderRequest(
                /** 商品ID（必填） */
                @NotBlank(message = "商品ID不能为空") String productId,

                /** 购买数量（最小为1） */
                @Min(value = 1, message = "购买数量至少为1") int quantity) {
}