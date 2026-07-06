package com.hfut.cat_adoption_system.dto;

/**
 * 猫咪识别候选结果DTO
 * 
 * 用于封装猫咪面部识别后的候选匹配结果，包含匹配猫咪的基本信息、
 * 匹配照片信息、置信度等数据，支持识别结果的展示和确认。
 * 使用Java Record实现，自动生成getter、equals、hashCode等方法。
 */
public record RecognitionCandidate(
                /** 候选猫咪ID */
                String catId,

                /** 候选猫咪名称 */
                String catName,

                /** 候选猫咪封面图片URL */
                String coverUrl,

                /** 匹配到的照片ID */
                String matchedPhotoId,

                /** 匹配到的照片URL */
                String matchedPhotoUrl,

                /** 拍摄角度编码（如正面、侧面等） */
                String angleCode,

                /** 匹配置信度（0-1之间，值越高匹配度越高） */
                double confidence,

                /** 匹配原因说明 */
                String reason) {
}