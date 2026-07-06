package com.hfut.cat_adoption_system.dto;

import java.time.LocalDateTime;

/**
 * 猫咪时间线事件DTO
 * 
 * 用于展示猫咪生命历程中的重要事件，如发现、治疗、认养等
 */
public record CatTimelineEvent(
                /** 事件类型（如：发现、体检、治疗、疫苗、认养等） */
                String eventType,
                /** 事件标题 */
                String title,
                /** 事件描述 */
                String description,
                /** 事件发生时间 */
                LocalDateTime eventTime) {
}