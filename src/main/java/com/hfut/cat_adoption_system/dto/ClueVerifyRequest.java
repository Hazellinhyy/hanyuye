package com.hfut.cat_adoption_system.dto;

import com.hfut.cat_adoption_system.model.VerifyResult;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 猫咪线索审核请求DTO
 * 
 * 用于审核学生提交的流浪猫线索
 */
public record ClueVerifyRequest(
                /** 审核结果，必填字段（如：通过、不通过、需进一步核实等） */
                @NotNull VerifyResult verifyResult,
                /** 审核意见，必填字段 */
                @NotBlank String verifyComment) {
}