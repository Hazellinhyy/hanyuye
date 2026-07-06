package com.hfut.cat_adoption_system.dto;

import java.util.List;

/**
 * 猫咪识别结果DTO
 * 
 * 用于封装猫咪面部识别的完整结果，包含识别过程中的样本数量、
 * 特征模型版本以及候选匹配结果列表，支持识别结果的完整展示和分析。
 * 使用Java Record实现，自动生成getter、equals、hashCode等方法。
 */
public record RecognitionResult(
                /** 参与识别的样本照片数量 */
                int sampleCount,

                /** 特征提取模型版本 */
                String featureVersion,

                /** 候选匹配结果列表（按置信度排序） */
                List<RecognitionCandidate> candidates) {
}