package com.hfut.cat_adoption_system.mapper;

import com.hfut.cat_adoption_system.model.DonationRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 捐赠记录Mapper接口
 * 
 * 提供捐赠记录的增删改查操作，支持按用户查询和状态更新功能。
 */
@Mapper
public interface DonationRecordMapper {

        /**
         * 查询捐赠记录列表
         * 
         * 支持按用户ID筛选，关联用户表获取捐赠人姓名，按捐赠时间倒序排列。
         * 
         * @param userId 用户ID（可选，为空则查询全部）
         * @return 捐赠记录列表
         */
        @ConstructorArgs({
                        @Arg(column = "donation_id", javaType = String.class),
                        @Arg(column = "user_id", javaType = String.class),
                        @Arg(column = "user_name", javaType = String.class),
                        @Arg(column = "amount", javaType = java.math.BigDecimal.class),
                        @Arg(column = "channel_id", javaType = String.class),
                        @Arg(column = "donor_message", javaType = String.class),
                        @Arg(column = "donate_status", javaType = String.class),
                        @Arg(column = "created_at", javaType = java.time.LocalDateTime.class)
        })
        @Select("""
                        <script>
                        SELECT d.donation_id, d.user_id, u.user_name, d.amount, d.channel_id,
                               d.donor_message, d.donate_status, d.created_at
                        FROM t_donation_record d
                        JOIN t_user u ON u.user_id = d.user_id
                        WHERE 1 = 1
                        <if test="userId != null and userId != ''">AND d.user_id = #{userId}</if>
                        ORDER BY d.created_at DESC
                        </script>
                        """)
        List<DonationRecord> findAll(@Param("userId") String userId);

        /**
         * 插入捐赠记录
         * 
         * @param record 捐赠记录对象
         */
        @Insert("""
                        INSERT INTO t_donation_record (donation_id, user_id, amount, channel_id, donor_message, donate_status, created_at)
                        VALUES (#{donationId}, #{userId}, #{amount}, #{channelId}, #{donorMessage}, #{donateStatus}, #{createdAt})
                        """)
        void insert(DonationRecord record);

        /**
         * 更新捐赠状态
         * 
         * @param donationId 捐赠记录ID
         * @param status     新状态
         * @return 更新的记录数
         */
        @Update("UPDATE t_donation_record SET donate_status = #{status} WHERE donation_id = #{donationId}")
        int updateStatus(@Param("donationId") String donationId, @Param("status") String status);
}