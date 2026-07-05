package com.hfut.cat_adoption_system.mapper;

import com.hfut.cat_adoption_system.dto.DictItemInfo;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface DictItemMapper {
    @ConstructorArgs({
            @Arg(column = "id", javaType = Long.class),
            @Arg(column = "dict_type", javaType = String.class),
            @Arg(column = "dict_label", javaType = String.class),
            @Arg(column = "dict_value", javaType = String.class),
            @Arg(column = "sort_order", javaType = Integer.class),
            @Arg(column = "enabled", javaType = Boolean.class),
            @Arg(column = "remark", javaType = String.class),
            @Arg(column = "create_time", javaType = LocalDateTime.class),
            @Arg(column = "update_time", javaType = LocalDateTime.class)
    })
    @Select("""
            <script>
            SELECT id, dict_type, item_label AS dict_label,
                   item_code AS dict_value,
                   sort_order, enabled, remark, create_time, update_time
            FROM dict_item
            WHERE COALESCE(deleted, 0) = 0
            <if test="dictType != null and dictType != ''">AND dict_type = #{dictType}</if>
            <if test="enabledOnly">AND enabled = 1</if>
            ORDER BY dict_type ASC, sort_order ASC, id ASC
            </script>
            """)
    List<DictItemInfo> findAll(@Param("dictType") String dictType, @Param("enabledOnly") boolean enabledOnly);

    @Select("SELECT COUNT(*) FROM dict_item WHERE dict_type = #{dictType} AND item_code = #{dictValue} AND COALESCE(deleted,0)=0")
    int countByTypeValue(@Param("dictType") String dictType, @Param("dictValue") String dictValue);

    @Insert("""
            INSERT INTO dict_item
            (dict_type, item_code, item_label, sort_order, enabled, remark, status, deleted,
             create_by, update_by, create_time, update_time)
            VALUES
            (#{dictType}, #{dictValue}, #{dictLabel}, #{sortOrder}, #{enabled}, #{remark}, 'VALID', 0,
             #{operatorId}, #{operatorId}, #{createdAt}, #{createdAt})
            """)
    int insert(@Param("dictType") String dictType,
               @Param("dictLabel") String dictLabel,
               @Param("dictValue") String dictValue,
               @Param("sortOrder") Integer sortOrder,
               @Param("enabled") Boolean enabled,
               @Param("remark") String remark,
               @Param("operatorId") String operatorId,
               @Param("createdAt") LocalDateTime createdAt);

    @Update("""
            UPDATE dict_item
            SET item_code = #{dictValue}, item_label = #{dictLabel}, sort_order = #{sortOrder},
                enabled = #{enabled}, remark = #{remark}, update_by = #{operatorId}, update_time = #{updatedAt}
            WHERE id = #{id} AND COALESCE(deleted, 0) = 0
            """)
    int update(@Param("id") Long id,
               @Param("dictLabel") String dictLabel,
               @Param("dictValue") String dictValue,
               @Param("sortOrder") Integer sortOrder,
               @Param("enabled") Boolean enabled,
               @Param("remark") String remark,
               @Param("operatorId") String operatorId,
               @Param("updatedAt") LocalDateTime updatedAt);

    @Update("UPDATE dict_item SET enabled = #{enabled}, update_by = #{operatorId}, update_time = #{updatedAt} WHERE id = #{id} AND COALESCE(deleted,0)=0")
    int updateEnabled(@Param("id") Long id, @Param("enabled") boolean enabled, @Param("operatorId") String operatorId, @Param("updatedAt") LocalDateTime updatedAt);

    @Update("UPDATE dict_item SET deleted = 1, update_by = #{operatorId}, update_time = #{updatedAt} WHERE id = #{id}")
    int delete(@Param("id") Long id, @Param("operatorId") String operatorId, @Param("updatedAt") LocalDateTime updatedAt);
}
