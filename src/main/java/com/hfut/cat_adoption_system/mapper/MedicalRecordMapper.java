package com.hfut.cat_adoption_system.mapper;

import com.hfut.cat_adoption_system.model.HealthLevel;
import com.hfut.cat_adoption_system.model.MedicalRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 医疗记录Mapper接口
 * 
 * 提供猫咪医疗记录的完整管理功能，包括体检、疫苗接种、绝育手术等记录的增删改查。
 * 支持多条件筛选、统计分析和逻辑删除，确保猫咪健康档案的完整性和可追溯性。
 */
@Mapper
public interface MedicalRecordMapper {

        /**
         * 查询医疗记录列表
         * 
         * 支持按猫咪ID筛选，返回未删除的记录，按创建时间倒序排列。
         * 
         * @param catId 猫咪ID（可选，为空则查询所有）
         * @return 医疗记录列表
         */
        @Select("""
                        <script>
                        SELECT medical_id, cat_id, check_date, hospital, health_level, vaccinated, sterilized, treatment, doctor_note, attachment_url, created_at
                        FROM t_medical_record
                        WHERE 1 = 1
                        AND COALESCE(deleted, 0) = 0
                        <if test="catId != null and catId != ''">AND cat_id = #{catId}</if>
                        ORDER BY created_at DESC
                        </script>
                        """)
        List<MedicalRecord> findAll(@Param("catId") String catId);

        /**
         * 查询医院用户提交的医疗记录
         * 
         * 用于医院用户查看自己提交的记录，支持分页限制返回数量。
         * 
         * @param hospitalUserId 医院用户ID
         * @param limit          返回数量限制（可选）
         * @return 医疗记录列表
         */
        @Select("""
                        <script>
                        SELECT medical_id, cat_id, check_date, hospital, health_level, vaccinated, sterilized, treatment, doctor_note, attachment_url, created_at
                        FROM t_medical_record
                        WHERE COALESCE(deleted, 0) = 0
                        <if test="hospitalUserId != null and hospitalUserId != ''">AND hospital_user_id = #{hospitalUserId}</if>
                        ORDER BY created_at DESC
                        <if test="limit != null">LIMIT #{limit}</if>
                        </script>
                        """)
        List<MedicalRecord> findHospitalRecords(@Param("hospitalUserId") String hospitalUserId,
                        @Param("limit") Integer limit);

        /**
         * 插入医疗记录（基础版本）
         * 
         * 适用于简单场景的医疗记录插入，包含核心字段。
         * 
         * @param record 医疗记录对象
         */
        @Insert("""
                        INSERT INTO t_medical_record (medical_id, cat_id, check_date, hospital, health_level, vaccinated, sterilized, treatment, doctor_note, created_at)
                        VALUES (#{medicalId}, #{catId}, #{checkDate}, #{hospital}, #{healthLevel}, #{vaccinated}, #{sterilized}, #{treatment}, #{doctorNote}, #{createdAt})
                        """)
        void insert(MedicalRecord record);

        /**
         * 插入医疗记录（管理员版本）
         * 
         * 完整的医疗记录插入，包含费用、附件、异常标记等扩展字段，适用于管理员录入场景。
         * 
         * @param medicalId      医疗记录ID
         * @param catId          猫咪ID
         * @param recordDate     记录日期
         * @param hospital       医院名称
         * @param healthLevel    健康等级（A/B/C三级）
         * @param vaccinated     是否已接种疫苗
         * @param sterilized     是否已绝育
         * @param description    治疗描述
         * @param doctorNote     医生备注
         * @param recordType     记录类型（体检/疫苗/手术等）
         * @param hospitalUserId 医院用户ID
         * @param cost           费用金额
         * @param attachmentUrl  附件URL
         * @param abnormalFlag   是否存在异常
         * @param createdAt      创建时间
         */
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
                        @Param("attachmentUrl") String attachmentUrl,
                        @Param("abnormalFlag") boolean abnormalFlag,
                        @Param("createdAt") LocalDateTime createdAt);

        /**
         * 更新医疗记录（管理员版本）
         * 
         * 更新已存在的医疗记录，支持修改所有字段。
         * 
         * @param medicalId      医疗记录ID
         * @param recordDate     记录日期
         * @param hospital       医院名称
         * @param healthLevel    健康等级
         * @param vaccinated     是否已接种疫苗
         * @param sterilized     是否已绝育
         * @param description    治疗描述
         * @param doctorNote     医生备注
         * @param recordType     记录类型
         * @param hospitalUserId 医院用户ID
         * @param cost           费用金额
         * @param attachmentUrl  附件URL
         * @param abnormalFlag   是否存在异常
         * @param updatedAt      更新时间
         * @return 更新的记录数
         */
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
                        @Param("attachmentUrl") String attachmentUrl,
                        @Param("abnormalFlag") boolean abnormalFlag,
                        @Param("updatedAt") LocalDateTime updatedAt);

        /**
         * 作废医疗记录（逻辑删除）
         * 
         * 将记录标记为已删除，同时在医生备注中追加作废原因，保留历史追溯。
         * 
         * @param medicalId  医疗记录ID
         * @param reason     作废原因
         * @param operatorId 操作人ID
         * @param updatedAt  更新时间
         * @return 更新的记录数
         */
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

        /**
         * 根据ID查询医疗记录
         * 
         * @param medicalId 医疗记录ID
         * @return 医疗记录对象（不存在返回null）
         */
        @Select("""
                        SELECT medical_id, cat_id, check_date, hospital, health_level, vaccinated, sterilized, treatment, doctor_note, attachment_url, created_at
                        FROM t_medical_record
                        WHERE medical_id = #{medicalId} AND COALESCE(deleted, 0) = 0
                        """)
        MedicalRecord findById(String medicalId);

        /**
         * 获取编号最大后缀值
         * 
         * 根据编号前缀查询最大数值后缀，用于自动生成连续的医疗记录编号。
         * 
         * @param prefix 编号前缀（如MR-）
         * @return 最大后缀值（无前缀时返回0）
         */
        @Select("""
                        SELECT COALESCE(MAX(CAST(SUBSTRING(medical_id, LENGTH(#{prefix}) + 1) AS UNSIGNED)), 0)
                        FROM t_medical_record
                        WHERE medical_id LIKE CONCAT(#{prefix}, '%')
                        """)
        int maxSuffixByPrefix(String prefix);

        /**
         * 统计某猫咪的医疗记录数量
         * 
         * @param catId 猫咪ID
         * @return 记录数量
         */
        @Select("SELECT COUNT(*) FROM t_medical_record WHERE cat_id = #{catId} AND COALESCE(deleted, 0) = 0")
        long countByCatId(String catId);

        /**
         * 统计所有活跃医疗记录数量
         * 
         * @return 活跃记录总数
         */
        @Select("SELECT COUNT(*) FROM t_medical_record WHERE COALESCE(deleted, 0) = 0")
        long countAllActive();

        /**
         * 统计某医院用户的医疗记录数量
         * 
         * @param hospitalUserId 医院用户ID
         * @return 记录数量
         */
        @Select("SELECT COUNT(*) FROM t_medical_record WHERE COALESCE(deleted, 0) = 0 AND hospital_user_id = #{hospitalUserId}")
        long countByHospitalUserId(String hospitalUserId);

        /**
         * 统计异常医疗记录数量
         * 
         * 异常判定条件：abnormal_flag为1（标记异常） 或 health_level为C级（健康状况差）
         * 
         * @return 异常记录数量
         */
        @Select("""
                        SELECT COUNT(*)
                        FROM t_medical_record
                        WHERE COALESCE(deleted, 0) = 0
                          AND (COALESCE(abnormal_flag, 0) = 1 OR health_level = 'C')
                        """)
        long countAbnormalActive();

        /**
         * 删除某猫咪的所有医疗记录（物理删除）
         * 
         * 当猫咪信息被删除时，级联删除其所有医疗记录。
         * 
         * @param catId 猫咪ID
         * @return 删除的记录数
         */
        @org.apache.ibatis.annotations.Delete("DELETE FROM t_medical_record WHERE cat_id = #{catId}")
        int deleteByCatId(String catId);
}