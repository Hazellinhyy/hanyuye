package com.hfut.cat_adoption_system.mapper;

import com.hfut.cat_adoption_system.mapper.type.StringListTypeHandler;
import com.hfut.cat_adoption_system.model.Cat;
import com.hfut.cat_adoption_system.model.CatStatus;
import com.hfut.cat_adoption_system.model.HealthLevel;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CatMapper {
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
            <script>
            SELECT c.cat_id, c.cat_name, c.found_place, c.found_date, c.gender, c.color, c.age_estimate, c.personality,
                   COALESCE(m.health_level, 'B') AS health_level,
                   COALESCE(m.sterilized, 0) AS sterilized,
                   COALESCE(m.vaccinated, 0) AS vaccinated,
                   c.cat_status, c.cover_url,
                   (SELECT GROUP_CONCAT(ct.tag_name ORDER BY ct.tag_name SEPARATOR ',') FROM cat_tag ct WHERE ct.cat_id = c.cat_id) AS tags,
                   c.description, c.created_at, c.updated_at
            FROM t_cat c
            LEFT JOIN t_medical_record m ON m.medical_id = (
                SELECT mr.medical_id FROM t_medical_record mr
                WHERE mr.cat_id = c.cat_id AND COALESCE(mr.deleted, 0) = 0
                ORDER BY mr.check_date DESC, mr.created_at DESC, mr.medical_id DESC
                LIMIT 1
            )
            WHERE COALESCE(c.deleted, 0) = 0
            <if test="status != null">AND c.cat_status = #{status}</if>
            <if test="keyword != null and keyword != ''">
                AND (c.cat_name LIKE CONCAT('%', #{keyword}, '%')
                    OR c.found_place LIKE CONCAT('%', #{keyword}, '%')
                    OR c.color LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            ORDER BY c.updated_at DESC
            </script>
            """)
    List<Cat> findAll(@Param("status") CatStatus status, @Param("keyword") String keyword);

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
            <script>
            SELECT c.cat_id, c.cat_name, c.found_place, c.found_date, c.gender, c.color, c.age_estimate, c.personality,
                   COALESCE(m.health_level, 'B') AS health_level,
                   COALESCE(m.sterilized, 0) AS sterilized,
                   COALESCE(m.vaccinated, 0) AS vaccinated,
                   c.cat_status, c.cover_url,
                   (SELECT GROUP_CONCAT(ct.tag_name ORDER BY ct.tag_name SEPARATOR ',') FROM cat_tag ct WHERE ct.cat_id = c.cat_id) AS tags,
                   c.description, c.created_at, c.updated_at
            FROM t_cat c
            LEFT JOIN t_medical_record m ON m.medical_id = (
                SELECT mr.medical_id FROM t_medical_record mr
                WHERE mr.cat_id = c.cat_id AND COALESCE(mr.deleted, 0) = 0
                ORDER BY mr.check_date DESC, mr.created_at DESC, mr.medical_id DESC
                LIMIT 1
            )
            WHERE COALESCE(c.deleted, 0) = 0
              AND c.cat_status = 'ADOPTABLE'
            <if test="keyword != null and keyword != ''">
                AND (c.cat_name LIKE CONCAT('%', #{keyword}, '%')
                    OR c.found_place LIKE CONCAT('%', #{keyword}, '%')
                    OR c.color LIKE CONCAT('%', #{keyword}, '%')
                    OR c.personality LIKE CONCAT('%', #{keyword}, '%')
                    OR EXISTS (SELECT 1 FROM cat_tag kt WHERE kt.cat_id = c.cat_id AND kt.tag_name LIKE CONCAT('%', #{keyword}, '%')))
            </if>
            <if test="gender != null and gender != ''">AND c.gender = #{gender}</if>
            <if test="healthLevel != null">AND COALESCE(m.health_level, 'B') = #{healthLevel}</if>
            <if test="sterilized != null">AND COALESCE(m.sterilized, 0) = #{sterilized}</if>
            <if test="vaccinated != null">AND COALESCE(m.vaccinated, 0) = #{vaccinated}</if>
            <if test="tag != null and tag != ''">AND EXISTS (SELECT 1 FROM cat_tag ft WHERE ft.cat_id = c.cat_id AND ft.tag_name = #{tag})</if>
            ORDER BY c.updated_at DESC
            </script>
            """)
    List<Cat> findPublic(@Param("keyword") String keyword,
                         @Param("gender") String gender,
                         @Param("healthLevel") HealthLevel healthLevel,
                         @Param("sterilized") Boolean sterilized,
                         @Param("vaccinated") Boolean vaccinated,
                         @Param("tag") String tag);

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
            <script>
            SELECT c.cat_id, c.cat_name, c.found_place, c.found_date, c.gender, c.color, c.age_estimate, c.personality,
                   COALESCE(m.health_level, 'B') AS health_level,
                   COALESCE(m.sterilized, 0) AS sterilized,
                   COALESCE(m.vaccinated, 0) AS vaccinated,
                   c.cat_status, c.cover_url,
                   (SELECT GROUP_CONCAT(ct.tag_name ORDER BY ct.tag_name SEPARATOR ',') FROM cat_tag ct WHERE ct.cat_id = c.cat_id) AS tags,
                   c.description, c.created_at, c.updated_at
            FROM t_cat c
            LEFT JOIN t_medical_record m ON m.medical_id = (
                SELECT mr.medical_id FROM t_medical_record mr
                WHERE mr.cat_id = c.cat_id AND COALESCE(mr.deleted, 0) = 0
                ORDER BY mr.check_date DESC, mr.created_at DESC, mr.medical_id DESC
                LIMIT 1
            )
            WHERE COALESCE(c.deleted, 0) = 0
            <if test="status != null">AND c.cat_status = #{status}</if>
            <if test="healthLevel != null">AND COALESCE(m.health_level, 'B') = #{healthLevel}</if>
            <if test="gender != null and gender != ''">AND c.gender = #{gender}</if>
            <if test="keyword != null and keyword != ''">
                AND (c.cat_id LIKE CONCAT('%', #{keyword}, '%')
                    OR c.cat_name LIKE CONCAT('%', #{keyword}, '%')
                    OR c.found_place LIKE CONCAT('%', #{keyword}, '%')
                    OR c.color LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            ORDER BY c.updated_at DESC
            <if test="limit != null">LIMIT #{limit}</if>
            <if test="offset != null">OFFSET #{offset}</if>
            </script>
            """)
    List<Cat> findAdmin(@Param("status") CatStatus status,
                        @Param("healthLevel") HealthLevel healthLevel,
                        @Param("gender") String gender,
                        @Param("keyword") String keyword,
                        @Param("limit") Integer limit,
                        @Param("offset") Integer offset);

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
            FROM t_cat c
            LEFT JOIN t_medical_record m ON m.medical_id = (
                SELECT mr.medical_id FROM t_medical_record mr
                WHERE mr.cat_id = c.cat_id AND COALESCE(mr.deleted, 0) = 0
                ORDER BY mr.check_date DESC, mr.created_at DESC, mr.medical_id DESC
                LIMIT 1
            )
            WHERE c.cat_id = #{catId}
            """)
    Cat findById(String catId);

    @Insert("""
            INSERT INTO t_cat (cat_id, cat_name, found_place, found_date, gender, color, age_estimate, personality,
                               health_level, sterilized, vaccinated, cat_status, cover_url, description, created_at, updated_at)
            VALUES (#{catId}, #{catName}, #{foundPlace}, #{foundDate}, #{gender}, #{color}, #{ageEstimate}, #{personality},
                    #{healthLevel}, #{sterilized}, #{vaccinated}, #{status}, #{coverUrl}, #{description}, #{createdAt}, #{updatedAt})
            """)
    void insert(Cat cat);

    @Insert("""
            INSERT INTO cat_tag (cat_id, tag_name)
            VALUES (#{catId}, #{tagName})
            """)
    void insertTag(@Param("catId") String catId, @Param("tagName") String tagName);

    @Delete("DELETE FROM cat_tag WHERE cat_id = #{catId}")
    void deleteTags(String catId);

    @Update("UPDATE t_cat SET cat_status = #{status}, updated_at = #{updatedAt} WHERE cat_id = #{catId}")
    void updateStatus(@Param("catId") String catId, @Param("status") CatStatus status, @Param("updatedAt") LocalDateTime updatedAt);

    @Delete("DELETE FROM t_cat WHERE cat_id = #{catId}")
    int delete(String catId);

    @Update("UPDATE t_cat SET deleted = 1, updated_at = #{updatedAt} WHERE cat_id = #{catId}")
    int logicalDelete(@Param("catId") String catId, @Param("updatedAt") LocalDateTime updatedAt);

    @Update("""
            UPDATE t_cat
            SET cat_name = #{catName}, found_place = #{foundPlace}, found_date = #{foundDate}, gender = #{gender},
                color = #{color}, age_estimate = #{ageEstimate}, personality = #{personality},
                cat_status = #{status}, cover_url = #{coverUrl},
                description = #{description}, updated_at = #{updatedAt}
            WHERE cat_id = #{catId}
            """)
    void update(Cat cat);

    @Update("""
            UPDATE t_cat
            SET cat_status = #{status}, updated_at = #{updatedAt}
            WHERE cat_id = #{catId}
            """)
    void updateHealth(@Param("catId") String catId,
                      @Param("healthLevel") HealthLevel healthLevel,
                      @Param("sterilized") boolean sterilized,
                      @Param("vaccinated") boolean vaccinated,
                      @Param("status") CatStatus status,
                      @Param("updatedAt") LocalDateTime updatedAt);

    @Select("SELECT COUNT(*) FROM t_cat WHERE COALESCE(deleted, 0) = 0")
    long countAll();

    @Select("SELECT COUNT(*) FROM t_cat WHERE COALESCE(deleted, 0) = 0 AND cat_status = #{status}")
    long countByStatus(CatStatus status);

    @Select("SELECT COUNT(*) FROM t_cat WHERE COALESCE(deleted, 0) = 0 AND cat_status = #{status}")
    long countActiveByStatus(CatStatus status);

    @Select("""
            SELECT COUNT(*)
            FROM t_cat c
            LEFT JOIN t_medical_record m ON m.medical_id = (
                SELECT mr.medical_id FROM t_medical_record mr
                WHERE mr.cat_id = c.cat_id AND COALESCE(mr.deleted, 0) = 0
                ORDER BY mr.check_date DESC, mr.created_at DESC, mr.medical_id DESC
                LIMIT 1
            )
            WHERE COALESCE(c.deleted, 0) = 0
              AND COALESCE(m.vaccinated, 0) = 0
            """)
    long countPendingVaccine();

    @Select("""
            SELECT COUNT(*)
            FROM t_cat c
            LEFT JOIN t_medical_record m ON m.medical_id = (
                SELECT mr.medical_id FROM t_medical_record mr
                WHERE mr.cat_id = c.cat_id AND COALESCE(mr.deleted, 0) = 0
                ORDER BY mr.check_date DESC, mr.created_at DESC, mr.medical_id DESC
                LIMIT 1
            )
            WHERE COALESCE(c.deleted, 0) = 0
              AND COALESCE(m.sterilized, 0) = 0
            """)
    long countPendingSterilization();

    @Select("""
            SELECT COALESCE(MAX(CAST(SUBSTRING(cat_id, 10) AS SIGNED)), 0)
            FROM t_cat
            WHERE cat_id LIKE CONCAT(#{prefix}, '%')
            """)
    int maxSuffixByPrefix(String prefix);

    @Select("""
            SELECT COUNT(*)
            FROM t_cat c
            JOIN t_medical_record m ON m.medical_id = (
                SELECT mr.medical_id FROM t_medical_record mr
                WHERE mr.cat_id = c.cat_id
                ORDER BY mr.check_date DESC, mr.created_at DESC, mr.medical_id DESC
                LIMIT 1
            )
            WHERE COALESCE(c.deleted, 0) = 0 AND m.sterilized = 1
            """)
    long countSterilized();

    @Select("""
            SELECT COUNT(*)
            FROM t_cat c
            JOIN t_medical_record m ON m.medical_id = (
                SELECT mr.medical_id FROM t_medical_record mr
                WHERE mr.cat_id = c.cat_id
                ORDER BY mr.check_date DESC, mr.created_at DESC, mr.medical_id DESC
                LIMIT 1
            )
            WHERE COALESCE(c.deleted, 0) = 0 AND m.vaccinated = 1
            """)
    long countVaccinated();

    @Select("SELECT found_place, COUNT(*) AS count FROM t_cat GROUP BY found_place ORDER BY count DESC, found_place ASC")
    List<com.hfut.cat_adoption_system.dto.LocationStat> countByLocation();
}
