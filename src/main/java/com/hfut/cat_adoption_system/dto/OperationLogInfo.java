package com.hfut.cat_adoption_system.dto;

import java.time.LocalDateTime;

public record OperationLogInfo(
        Long id,
        String operatorId,
        String operatorName,
        String operationType,
        String bizType,
        String bizId,
        String beforeData,
        String afterData,
        String requestIp,
        String remark,
        LocalDateTime createTime
) {
}
