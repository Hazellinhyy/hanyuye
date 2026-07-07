package com.hfut.cat_adoption_system.mapper;

import com.hfut.cat_adoption_system.mapper.type.StringListTypeHandler;
import com.hfut.cat_adoption_system.model.Cat;
import com.hfut.cat_adoption_system.model.CatStatus;
import com.hfut.cat_adoption_system.model.HealthLevel;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 猫咪信息Mapper接口
 * 
 * 提供猫咪数据的增删改查操作，包括基础信息管理、状态更新、健康统计等功能。
 * 猫咪健康状态（绝育、疫苗）通过关联最新的医疗记录获取。
 */
@Mapper
public interface CatMapper {

        /**
         * 查询猫咪列表（管理员端基础查询）
         * 
         * 通过猫咪状态和关键词筛选，关联最新医疗记录获取健康信息，按更新时间倒序排列。
         * 
         * @param status  猫咪状态（可选）
         * @param keyword 关键词（支持猫咪名称、发现地点、颜色模糊搜索）
         * @return 猫咪列表
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

        /**
         * 查询可领养猫咪列表（公共前端使用）
         * 
         * 仅返回状态为 ADOPTABLE 的猫咪，支持多条件筛选（性别、健康等级、绝育状态、疫苗状态、标签）。
         * 
         * @param keyword     关键词（支持名称、地点、颜色、性格搜索，以及标签匹配）
         * @param gender      性别筛选
         * @param healthLevel 健康等级筛选
         * @param sterilized  是否已绝育
         * @param vaccinated  是否已接种疫苗
         * @param tag         标签筛选
         * @return 可领养猫咪列表
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

        /**
         * 查询猫咪列表（管理员分页查询）
         * 
         * 支持状态、健康等级、性别筛选，支持分页和关键词搜索。
         * 
         * @param status      猫咪状态
         * @param healthLevel 健康等级
         * @param gender      性别
         * @param keyword     关键词
         * @param limit       每页数量
         * @param offset      起始偏移
         * @return 猫咪列表
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

        /**
         * 根据猫咪ID查询单个猫咪详情
         * 
         * @param catId 猫咪ID
         * @return 猫咪信息对象
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

        /**
         * 插入新猫咪记录
         * 
         * @param cat 猫咪信息对象
         */
        @Insert("""
                        INSERT INTO t_cat (cat_id, cat_name, found_place, found_date, gender, color, age_estimate, personality,
                                           health_level, sterilized, vaccinated, cat_status, cover_url, description, created_at, updated_at)
                        VALUES (#{catId}, #{catName}, #{foundPlace}, #{foundDate}, #{gender}, #{color}, #{ageEstimate}, #{personality},
                                #{healthLevel}, #{sterilized}, #{vaccinated}, #{status}, #{coverUrl}, #{description}, #{createdAt}, #{updatedAt})
                        """)
        void insert(Cat cat);

        /**
         * 插入猫咪标签关联记录
         * 
         * @param catId   猫咪ID
         * @param tagName 标签名称
         */
        @Insert("""
                        INSERT INTO cat_tag (cat_id, tag_name)
                        VALUES (#{catId}, #{tagName})
                        """)
        void insertTag(@Param("catId") String catId, @Param("tagName") String tagName);

        /**
         * 删除猫咪的所有标签
         * 
         * @param catId 猫咪ID
         */
        @Delete("DELETE FROM cat_tag WHERE cat_id = #{catId}")
        void deleteTags(String catId);

        /**
         * 更新猫咪状态
         * 
         * @param catId     猫咪ID
         * @param status    新状态
         * @param updatedAt 更新时间
         */
        @Update("UPDATE t_cat SET cat_status = #{status}, updated_at = #{updatedAt} WHERE cat_id = #{catId}")
        void updateStatus(@Param("catId") String catId, @Param("status") CatStatus status,
                        @Param("updatedAt") LocalDateTime updatedAt);

        /**
         * 物理删除猫咪记录
         * 
         * @param catId 猫咪ID
         * @return 删除的记录数
         */
        @Delete("DELETE FROM t_cat WHERE cat_id = #{catId}")
        int delete(String catId);

        /**
         * 逻辑删除猫咪记录（标记删除）
         * 
         * @param catId     猫咪ID
         * @param updatedAt 更新时间
         * @return 删除的记录数
         */
        @Update("UPDATE t_cat SET deleted = 1, updated_at = #{updatedAt} WHERE cat_id = #{catId}")
        int logicalDelete(@Param("catId") String catId, @Param("updatedAt") LocalDateTime updatedAt);

        /**
         * 更新猫咪基本信息
         * 
         * @param cat 猫咪信息对象（包含更新的字段）
         */
        @Update("""
                        UPDATE t_cat
                        SET cat_name = #{catName}, found_place = #{foundPlace}, found_date = #{foundDate}, gender = #{gender},
                            color = #{color}, age_estimate = #{ageEstimate}, personality = #{personality},
                            cat_status = #{status}, cover_url = #{coverUrl},
                            description = #{description}, updated_at = #{updatedAt}
                        WHERE cat_id = #{catId}
                        """)
        void update(Cat cat);

        /**
         * 更新猫咪健康相关信息
         * 
         * @param catId       猫咪ID
         * @param healthLevel 健康等级
         * @param sterilized  是否绝育
         * @param vaccinated  是否接种疫苗
         * @param status      猫咪状态
         * @param updatedAt   更新时间
         */
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

        /**
         * 统计所有未删除的猫咪数量
         * 
         * @return 猫咪总数
         */
        @Select("SELECT COUNT(*) FROM t_cat WHERE COALESCE(deleted, 0) = 0")
        long countAll();

        /**
         * 按状态统计猫咪数量
         * 
         * @param status 猫咪状态
         * @return 该状态的猫咪数量
         */
        @Select("SELECT COUNT(*) FROM t_cat WHERE COALESCE(deleted, 0) = 0 AND cat_status = #{status}")
        long countByStatus(CatStatus status);

        /**
         * 按状态统计活跃猫咪数量（别名方法）
         * 
         * @param status 猫咪状态
         * @return 该状态的猫咪数量
         */
        @Select("SELECT COUNT(*) FROM t_cat WHERE COALESCE(deleted, 0) = 0 AND cat_status = #{status}")
        long countActiveByStatus(CatStatus status);

        /**
         * 统计未接种疫苗的猫咪数量
         * 
         * 通过关联最新医疗记录判断疫苗接种状态。
         * 
         * @return 未接种疫苗的猫咪数量
         */
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

        /**
         * 统计未绝育的猫咪数量
         * 
         * 通过关联最新医疗记录判断绝育状态。
         * 
         * @return 未绝育的猫咪数量
         */
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

        /**
         * 获取猫咪ID最大后缀值（用于生成新ID）
         * 
         * @param prefix ID前缀
         * @return 最大后缀数值
         */
        @Select("""
                        SELECT COALESCE(MAX(CAST(SUBSTRING(cat_id, 10) AS SIGNED)), 0)
                        FROM t_cat
                        WHERE cat_id LIKE CONCAT(#{prefix}, '%')
                        """)
        int maxSuffixByPrefix(String prefix);

        /**
         * 统计已绝育的猫咪数量
         * 
         * @return 已绝育猫咪数量
         */
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

        /**
         * 统计已接种疫苗的猫咪数量
         * 
         * @return 已接种疫苗猫咪数量
         */
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

        /**
         * 按发现地点统计猫咪数量
         * 
         * @return 地点统计列表
         */
        @Select("SELECT found_place, COUNT(*) AS count FROM t_cat GROUP BY found_place ORDER BY count DESC, found_place ASC")
        List<com.hfut.cat_adoption_system.dto.LocationStat> countByLocation();
}
