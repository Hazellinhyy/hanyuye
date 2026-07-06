package com.hfut.cat_adoption_system.dto;

import java.time.LocalDateTime;

/**
 * 操作日志信息DTO
 * 
 * 用于封装系统操作日志的详细信息，记录用户在系统中的各类操作行为，
 * 支持日志查询、审计追溯和安全监控。
 * 使用Java Record实现，自动生成getter、equals、hashCode等方法。
 */
public record OperationLogInfo(
                /** 日志ID */
                Long id,

                /** 操作者ID */
                String operatorId,

                /** 操作者姓名 */
                String operatorName,

                /** 操作类型（如新增、修改、删除、查询等） */
                String operationType,

                /** 业务类型（如用户管理、猫咪管理、医疗记录等） */
                String bizType,

                /** 业务ID（被操作对象的唯一标识） */
                String bizId,

                /** 操作前数据（JSON格式，记录变更前的状态） */
                String beforeData,

                /** 操作后数据（JSON格式，记录变更后的状态） */
                String afterData,

                /** 请求IP地址 */
                String requestIp,

                /** 备注信息 */
                String remark,

                /** 操作时间 */
                LocalDateTime createTime) {
}