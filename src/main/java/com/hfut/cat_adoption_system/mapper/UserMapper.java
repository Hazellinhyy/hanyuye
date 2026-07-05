package com.hfut.cat_adoption_system.mapper;

import com.hfut.cat_adoption_system.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserMapper {
    String USER_COLUMNS = """
            user_id, user_name, school_no, phone, id_card, college, pet_experience,
            CASE UPPER(role_code)
                WHEN 'HOSPITAL_USER' THEN 'HOSPITAL'
                WHEN 'MEDICAL' THEN 'HOSPITAL'
                WHEN 'DOCTOR' THEN 'HOSPITAL'
                WHEN 'PARTNER_HOSPITAL' THEN 'HOSPITAL'
                WHEN '合作医院' THEN 'HOSPITAL'
                WHEN '医疗协作用户' THEN 'HOSPITAL'
                WHEN '医院用户' THEN 'HOSPITAL'
                ELSE role_code
            END AS role,
            status AS enabled, created_at
            """;

    @Select("SELECT " + USER_COLUMNS + " FROM t_user ORDER BY created_at DESC")
    List<User> findAll();

    @Select("""
            <script>
            SELECT """ + " " + USER_COLUMNS + """
            FROM t_user
            WHERE 1 = 1
            <if test="role != null">AND role_code = #{role}</if>
            <if test="enabled != null">AND status = #{enabled}</if>
            <if test="keyword != null and keyword != ''">
              AND (user_id LIKE CONCAT('%', #{keyword}, '%')
                   OR user_name LIKE CONCAT('%', #{keyword}, '%')
                   OR school_no LIKE CONCAT('%', #{keyword}, '%')
                   OR phone LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            ORDER BY created_at DESC
            </script>
            """)
    List<User> findAdminUsers(@Param("role") com.hfut.cat_adoption_system.model.Role role,
                              @Param("enabled") Boolean enabled,
                              @Param("keyword") String keyword);

    @Select("SELECT " + USER_COLUMNS + " FROM t_user WHERE user_id = #{userId}")
    User findById(String userId);

    @Select("SELECT " + USER_COLUMNS + " FROM t_user WHERE school_no = #{account} OR phone = #{account} OR user_id = #{account} LIMIT 1")
    User findByAccount(String account);

    @Select("SELECT password_hash FROM t_user WHERE user_id = #{userId}")
    String findPasswordHash(String userId);

    @Select("SELECT token_version FROM t_user WHERE user_id = #{userId}")
    int findTokenVersion(String userId);

    @Select("SELECT COUNT(*) FROM t_user WHERE school_no = #{schoolNo}")
    int countBySchoolNo(String schoolNo);

    @Select("SELECT COUNT(*) FROM t_user WHERE school_no = #{schoolNo} AND user_id <> #{userId}")
    int countBySchoolNoExcept(@Param("schoolNo") String schoolNo, @Param("userId") String userId);

    @Select("""
            SELECT COALESCE(MAX(CAST(SUBSTRING(user_id, LENGTH(#{prefix}) + 1) AS UNSIGNED)), 0)
            FROM t_user
            WHERE user_id LIKE CONCAT(#{prefix}, '%')
            """)
    int maxSuffixByPrefix(String prefix);

    @Insert("""
            INSERT INTO t_user (user_id, user_name, school_no, phone, id_card, college, pet_experience, role_code, status, created_at)
            VALUES (#{userId}, #{userName}, #{schoolNo}, #{phone}, #{idCard}, #{college}, #{petExperience}, #{role}, #{enabled}, #{createdAt})
            """)
    void insert(User user);

    @Update("UPDATE t_user SET password_hash = #{passwordHash}, token_version = token_version + 1 WHERE user_id = #{userId}")
    void updatePassword(@Param("userId") String userId, @Param("passwordHash") String passwordHash);

    @Update("UPDATE t_user SET token_version = token_version + 1 WHERE user_id = #{userId}")
    void incrementTokenVersion(String userId);

    @Update("""
            UPDATE t_user
            SET user_name = #{userName}, phone = #{phone}, college = #{college}, pet_experience = #{petExperience}
            WHERE user_id = #{userId}
            """)
    void updateProfile(@Param("userId") String userId,
                       @Param("userName") String userName,
                       @Param("phone") String phone,
                       @Param("college") String college,
                       @Param("petExperience") String petExperience);

    @Update("""
            UPDATE t_user
            SET user_name = #{userName}, school_no = #{schoolNo}, phone = #{phone}, id_card = #{idCard},
                college = #{college}, pet_experience = #{petExperience}
            WHERE user_id = #{userId}
            """)
    int updateAdminUser(@Param("userId") String userId,
                        @Param("userName") String userName,
                        @Param("schoolNo") String schoolNo,
                        @Param("phone") String phone,
                        @Param("idCard") String idCard,
                        @Param("college") String college,
                        @Param("petExperience") String petExperience);

    @Update("UPDATE t_user SET role_code = #{role}, token_version = token_version + 1 WHERE user_id = #{userId}")
    int updateRole(@Param("userId") String userId, @Param("role") com.hfut.cat_adoption_system.model.Role role);

    @Update("UPDATE t_user SET status = #{enabled}, token_version = token_version + 1 WHERE user_id = #{userId}")
    int updateEnabled(@Param("userId") String userId, @Param("enabled") boolean enabled);
}
