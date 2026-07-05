package com.hfut.cat_adoption_system.mapper;

import com.hfut.cat_adoption_system.mapper.type.StringListTypeHandler;
import com.hfut.cat_adoption_system.model.Cat;
import com.hfut.cat_adoption_system.model.CatStatus;
import com.hfut.cat_adoption_system.model.HealthLevel;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface FavoriteMapper {
    @Insert("INSERT IGNORE INTO t_favorite (user_id, cat_id, created_at) VALUES (#{userId}, #{catId}, #{createdAt})")
    void insert(@Param("userId") String userId, @Param("catId") String catId, @Param("createdAt") LocalDateTime createdAt);

    @Delete("DELETE FROM t_favorite WHERE user_id = #{userId} AND cat_id = #{catId}")
    void delete(@Param("userId") String userId, @Param("catId") String catId);

    @Delete("DELETE FROM t_favorite WHERE cat_id = #{catId}")
    int deleteByCatId(String catId);

    @Select("SELECT COUNT(*) FROM t_favorite WHERE user_id = #{userId} AND cat_id = #{catId}")
    int count(@Param("userId") String userId, @Param("catId") String catId);

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
