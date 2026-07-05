package com.hfut.cat_adoption_system.mapper;

import com.hfut.cat_adoption_system.model.HealthLevel;
import com.hfut.cat_adoption_system.model.MedicalRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface MedicalRecordMapper {
    @Select("""
            <script>
            SELECT medical_id, cat_id, check_date, hospital, health_level, vaccinated, sterilized, treatment, doctor_note, created_at
            FROM t_medical_record
            WHERE 1 = 1
            AND COALESCE(deleted, 0) = 0
            <if test="catId != null and catId != ''">AND cat_id = #{catId}</if>
            ORDER BY created_at DESC
            </script>
            """)
    List<MedicalRecord> findAll(@Param("catId") String catId);

    @Select("""
            <script>
            SELECT medical_id, cat_id, check_date, hospital, health_level, vaccinated, sterilized, treatment, doctor_note, created_at
            FROM t_medical_record
            WHERE COALESCE(deleted, 0) = 0
            <if test="hospitalUserId != null and hospitalUserId != ''">AND hospital_user_id = #{hospitalUserId}</if>
            ORDER BY created_at DESC
            <if test="limit != null">LIMIT #{limit}</if>
            </script>
            """)
    List<MedicalRecord> findHospitalRecords(@Param("hospitalUserId") String hospitalUserId,
                                            @Param("limit") Integer limit);

    @Insert("""
            INSERT INTO t_medical_record (medical_id, cat_id, check_date, hospital, health_level, vaccinated, sterilized, treatment, doctor_note, created_at)
            VALUES (#{medicalId}, #{catId}, #{checkDate}, #{hospital}, #{healthLevel}, #{vaccinated}, #{sterilized}, #{treatment}, #{doctorNote}, #{createdAt})
            """)
    void insert(MedicalRecord record);

    @Insert("""
            INSERT INTO t_medical_record (medical_id, cat_id, check_date, hospital, health_level, vaccinated, sterilized,
                                          treatment, doctor_note, record_type, hospital_user_id, cost, attachment_url,
                                          abnormal_flag, create_by, update_by, created_at, updated_at, deleted)
            VALUES (#{medicalId}, #{catId}, #{recordDate}, #{hospital}, #{healthLevel}, #{vaccinated}, #{sterilized},
                    #{description}, #{doctorNote}, #{recordType}, #{hospitalUserId}, #{cost}, #{attachmentUrl},
                    #{abnormalFlag}, #{hospitalUserId}, #{hospitalUserId}, #{createdAt}, #{createdAt}, 0)
            """)
    void insertAdminRecord(@Param("medicalId") String medicalId,
                           @Param("catId") String catId,
                           @Param("recordDate") LocalDate recordDate,
                           @Param("hospital") String hospital,
                           @Param("healthLevel") HealthLevel healthLevel,
                           @Param("vaccinated") boolean vaccinated,
                           @Param("sterilized") boolean sterilized,
                           @Param("description") String description,
                           @Param("doctorNote") String doctorNote,
                           @Param("recordType") String recordType,
                           @Param("hospitalUserId") String hospitalUserId,
                           @Param("cost") BigDecimal cost,
                           @Param("attachmentUrl") String attachmentUrl,
                           @Param("abnormalFlag") boolean abnormalFlag,
                           @Param("createdAt") LocalDateTime createdAt);

    @Update("""
            UPDATE t_medical_record
            SET check_date = #{recordDate}, hospital = #{hospital}, health_level = #{healthLevel},
                vaccinated = #{vaccinated}, sterilized = #{sterilized}, treatment = #{description},
                doctor_note = #{doctorNote}, record_type = #{recordType}, hospital_user_id = #{hospitalUserId},
                cost = #{cost}, attachment_url = #{attachmentUrl}, abnormal_flag = #{abnormalFlag},
                update_by = #{hospitalUserId}, updated_at = #{updatedAt}
            WHERE medical_id = #{medicalId} AND COALESCE(deleted, 0) = 0
            """)
    int updateAdminRecord(@Param("medicalId") String medicalId,
                          @Param("recordDate") LocalDate recordDate,
                          @Param("hospital") String hospital,
                          @Param("healthLevel") HealthLevel healthLevel,
                          @Param("vaccinated") boolean vaccinated,
                          @Param("sterilized") boolean sterilized,
                          @Param("description") String description,
                          @Param("doctorNote") String doctorNote,
                          @Param("recordType") String recordType,
                          @Param("hospitalUserId") String hospitalUserId,
                          @Param("cost") BigDecimal cost,
                          @Param("attachmentUrl") String attachmentUrl,
                          @Param("abnormalFlag") boolean abnormalFlag,
                          @Param("updatedAt") LocalDateTime updatedAt);

    @Update("""
            UPDATE t_medical_record
            SET deleted = 1,
                doctor_note = CONCAT(COALESCE(doctor_note, ''), CASE WHEN COALESCE(doctor_note, '') = '' THEN '' ELSE '；' END, '作废原因：', #{reason}),
                update_by = #{operatorId}, updated_at = #{updatedAt}
            WHERE medical_id = #{medicalId} AND COALESCE(deleted, 0) = 0
            """)
    int voidRecord(@Param("medicalId") String medicalId,
                   @Param("reason") String reason,
                   @Param("operatorId") String operatorId,
                   @Param("updatedAt") LocalDateTime updatedAt);

    @Select("""
            SELECT medical_id, cat_id, check_date, hospital, health_level, vaccinated, sterilized, treatment, doctor_note, created_at
            FROM t_medical_record
            WHERE medical_id = #{medicalId} AND COALESCE(deleted, 0) = 0
            """)
    MedicalRecord findById(String medicalId);

    @Select("""
            SELECT COALESCE(MAX(CAST(SUBSTRING(medical_id, LENGTH(#{prefix}) + 1) AS UNSIGNED)), 0)
            FROM t_medical_record
            WHERE medical_id LIKE CONCAT(#{prefix}, '%')
            """)
    int maxSuffixByPrefix(String prefix);

    @Select("SELECT COUNT(*) FROM t_medical_record WHERE cat_id = #{catId} AND COALESCE(deleted, 0) = 0")
    long countByCatId(String catId);

    @Select("SELECT COUNT(*) FROM t_medical_record WHERE COALESCE(deleted, 0) = 0")
    long countAllActive();

    @Select("SELECT COUNT(*) FROM t_medical_record WHERE COALESCE(deleted, 0) = 0 AND hospital_user_id = #{hospitalUserId}")
    long countByHospitalUserId(String hospitalUserId);

    @Select("""
            SELECT COUNT(*)
            FROM t_medical_record
            WHERE COALESCE(deleted, 0) = 0
              AND (COALESCE(abnormal_flag, 0) = 1 OR health_level = 'C')
            """)
    long countAbnormalActive();

    @org.apache.ibatis.annotations.Delete("DELETE FROM t_medical_record WHERE cat_id = #{catId}")
    int deleteByCatId(String catId);
}
