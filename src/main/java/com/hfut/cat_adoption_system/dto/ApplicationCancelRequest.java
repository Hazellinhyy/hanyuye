package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 认养申请取消请求DTO
 * 
 * 用于用户取消已提交的认养申请
 */
public record ApplicationCancelRequest(
                /** 取消原因，必填字段 */
                @NotBlank String cancelReason) {
}