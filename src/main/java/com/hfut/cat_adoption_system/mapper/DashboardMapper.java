package com.hfut.cat_adoption_system.mapper;

import com.hfut.cat_adoption_system.dto.DashboardDistributionItem;
import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.annotations.ConstructorArgs;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 仪表盘统计Mapper接口
 * 
 * 提供系统后台仪表盘所需的各类数据分布统计，用于数据可视化展示。
 */
@Mapper
public interface DashboardMapper {

        /**
         * 统计猫咪状态分布
         * 
         * 按猫咪状态分组统计数量，用于仪表盘展示猫咪各状态的分布情况。
         * 
         * @return 状态分布列表（状态名称 + 数量）
         */
        @ConstructorArgs({
                        @Arg(column = "name", javaType = String.class),
                        @Arg(column = "count", javaType = long.class)
        })
        @Select("SELECT cat_status AS name, COUNT(*) AS count FROM t_cat WHERE COALESCE(deleted,0)=0 GROUP BY cat_status")
        List<DashboardDistributionItem> catStatus();

        /**
         * 统计领养申请状态分布
         * 
         * 按申请状态分组统计数量，用于仪表盘展示领养申请的处理进度分布。
         * 
         * @return 申请状态分布列表（状态名称 + 数量）
         */
        @ConstructorArgs({
                        @Arg(column = "name", javaType = String.class),
                        @Arg(column = "count", javaType = long.class)
        })
        @Select("SELECT apply_status AS name, COUNT(*) AS count FROM t_application WHERE COALESCE(deleted,0)=0 GROUP BY apply_status")
        List<DashboardDistributionItem> applicationStatus();

        /**
         * 统计回访任务状态分布
         * 
         * 按回访任务状态分组统计数量，用于仪表盘展示回访工作的完成情况。
         * 
         * @return 回访状态分布列表（状态名称 + 数量）
         */
        @ConstructorArgs({
                        @Arg(column = "name", javaType = String.class),
                        @Arg(column = "count", javaType = long.class)
        })
        @Select("SELECT status AS name, COUNT(*) AS count FROM followup_task WHERE COALESCE(deleted,0)=0 GROUP BY status")
        List<DashboardDistributionItem> followupStatus();

        /**
         * 统计预警类型分布
         * 
         * 按预警类型分组统计数量，用于仪表盘展示系统预警的类型分布情况。
         * 
         * @return 预警类型分布列表（预警类型名称 + 数量）
         */
        @ConstructorArgs({
                        @Arg(column = "name", javaType = String.class),
                        @Arg(column = "count", javaType = long.class)
        })
        @Select("SELECT warning_type AS name, COUNT(*) AS count FROM warning_record WHERE COALESCE(deleted,0)=0 GROUP BY warning_type")
        List<DashboardDistributionItem> warningType();
}