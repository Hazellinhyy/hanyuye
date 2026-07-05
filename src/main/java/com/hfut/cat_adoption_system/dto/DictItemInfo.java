package com.hfut.cat_adoption_system.dto;

import java.time.LocalDateTime;

public record DictItemInfo(
        Long id,
        String dictType,
        String dictLabel,
        String dictValue,
        Integer sortOrder,
        Boolean enabled,
        String remark,
        LocalDateTime createTime,
        LocalDateTime updateTime
) {
}
