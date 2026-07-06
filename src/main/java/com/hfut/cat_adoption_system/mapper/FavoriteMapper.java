package com.hfut.cat_adoption_system.mapper;

import com.hfut.cat_adoption_system.mapper.type.StringListTypeHandler;
import com.hfut.cat_adoption_system.model.Cat;
import com.hfut.cat_adoption_system.model.CatStatus;
import com.hfut.cat_adoption_system.model.HealthLevel;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户收藏Mapper接口
 * 
 * 提供用户收藏猫咪的增删查操作，支持用户收藏列表管理。
 */
@Mapper
public interface FavoriteMapper {

    /**
     * 插入收藏记录
     * 
     * 使用INSERT IGNORE避免重复收藏，已存在则忽略不报错。
     * 
     * @param userId    用户ID
     * @param catId     猫咪ID
     * @param createdAt 创建时间
     */
    @Insert("INSERT IGNORE INTO t_favorite (user_id, cat_id, created_at) VALUES (#{userId}, #{catId}, #{createdAt})")
    void insert(@Param("userId") String userId, @Param("catId") String catId,
            @Param("createdAt") LocalDateTime createdAt);

    /**
     * 删除用户对某猫咪的收藏
     * 
     * @param userId 用户ID
     * @param catId  猫咪ID
     */
    @Delete("DELETE FROM t_favorite WHERE user_id = #{userId} AND cat_id = #{catId}")
    void delete(@Param("userId") String userId, @Param("catId") String catId);

    /**
     * 删除某猫咪的所有收藏记录
     * 
     * 用于猫咪删除时级联删除相关收藏。
     * 
     * @param catId 猫咪ID
     * @return 删除的记录数
     */
    @Delete("DELETE FROM t_favorite WHERE cat_id = #{catId}")
    int deleteByCatId(String catId);

    /**
     * 检查用户是否已收藏某猫咪
     * 
     * @param userId 用户ID
     * @param catId  猫咪ID
     * @return 收藏数量（0表示未收藏，1表示已收藏）
     */
    @Select("SELECT COUNT(*) FROM t_favorite WHERE user_id = #{userId} AND cat_id = #{catId}")
    int count(@Param("userId") String userId, @Param("catId") String catId);

    /**
     * 查询用户收藏的猫咪列表
     * 
     * 关联猫咪表和最新医疗记录，获取猫咪完整信息（含健康状态），按收藏时间倒序排列。
     * 
     * @param userId 用户ID
     * @return 用户收藏的猫咪列表
     */
    @ConstructorArgs({
            @Arg(column = "cat_id", javaType = String.class),
            @Arg(column = "cat_name", javaType = String.class),
            @Arg(column = "found_place", javaType = String.class),
            @Arg(column = "found_date", javaType = java.time.LocalDate.class),
            @Arg(column = "gender", javaType = String.class),
            @Arg(column = "color", javaType = String.class),
            @Arg(column = "age_estimate", javaType = String.class),
            @Arg(column = "personality", javaType = String.class),
            @Arg(column = "health_level", javaType = HealthLevel.class),
            @Arg(column = "sterilized", javaType = boolean.class),
            @Arg(column = "vaccinated", javaType = boolean.class),
            @Arg(column = "cat_status", javaType = CatStatus.class),
            @Arg(column = "cover_url", javaType = String.class),
            @Arg(column = "tags", javaType = List.class, typeHandler = StringListTypeHandler.class),
            @Arg(column = "description", javaType = String.class),
            @Arg(column = "created_at", javaType = LocalDateTime.class),
            @Arg(column = "updated_at", javaType = LocalDateTime.class)
    })
    @Select("""
            SELECT c.cat_id, c.cat_name, c.found_place, c.found_date, c.gender, c.color, c.age_estimate, c.personality,
                   COALESCE(m.health_level, 'B') AS health_level,
                   COALESCE(m.sterilized, 0) AS sterilized,
                   COALESCE(m.vaccinated, 0) AS vaccinated,
                   c.cat_status, c.cover_url,
                   (SELECT GROUP_CONCAT(ct.tag_name ORDER BY ct.tag_name SEPARATOR ',') FROM cat_tag ct WHERE ct.cat_id = c.cat_id) AS tags,
                   c.description, c.created_at, c.updated_at
            FROM t_favorite f
            JOIN t_cat c ON c.cat_id = f.cat_id
            LEFT JOIN t_medical_record m ON m.medical_id = (
                SELECT mr.medical_id FROM t_medical_record mr
                WHERE mr.cat_id = c.cat_id AND COALESCE(mr.deleted, 0) = 0
                ORDER BY mr.check_date DESC, mr.created_at DESC, mr.medical_id DESC
                LIMIT 1
            )
            WHERE f.user_id = #{userId}
            ORDER BY f.created_at DESC
            """)
    List<Cat> findCatsByUser(String userId);
}