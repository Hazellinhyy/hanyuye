package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 操作原因请求DTO
 * 
 * 用于需要填写操作原因的场景，如作废记录、取消申请等操作
 */
public record ActionReasonRequest(
                /** 操作原因说明，必填字段 */
                @NotBlank String reason) {
}