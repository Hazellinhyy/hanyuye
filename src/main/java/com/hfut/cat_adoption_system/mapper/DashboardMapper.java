package com.hfut.cat_adoption_system.mapper;

import com.hfut.cat_adoption_system.dto.DashboardDistributionItem;
import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.annotations.ConstructorArgs;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DashboardMapper {
    @ConstructorArgs({
            @Arg(column = "name", javaType = String.class),
            @Arg(column = "count", javaType = long.class)
    })
    @Select("SELECT cat_status AS name, COUNT(*) AS count FROM t_cat WHERE COALESCE(deleted,0)=0 GROUP BY cat_status")
    List<DashboardDistributionItem> catStatus();

    @ConstructorArgs({
            @Arg(column = "name", javaType = String.class),
            @Arg(column = "count", javaType = long.class)
    })
    @Select("SELECT apply_status AS name, COUNT(*) AS count FROM t_application WHERE COALESCE(deleted,0)=0 GROUP BY apply_status")
    List<DashboardDistributionItem> applicationStatus();

    @ConstructorArgs({
            @Arg(column = "name", javaType = String.class),
            @Arg(column = "count", javaType = long.class)
    })
    @Select("SELECT status AS name, COUNT(*) AS count FROM followup_task WHERE COALESCE(deleted,0)=0 GROUP BY status")
    List<DashboardDistributionItem> followupStatus();

    @ConstructorArgs({
            @Arg(column = "name", javaType = String.class),
            @Arg(column = "count", javaType = long.class)
    })
    @Select("SELECT warning_type AS name, COUNT(*) AS count FROM warning_record WHERE COALESCE(deleted,0)=0 GROUP BY warning_type")
    List<DashboardDistributionItem> warningType();
}
