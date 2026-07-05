package com.hfut.cat_adoption_system.mapper;

import com.hfut.cat_adoption_system.model.DonationRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DonationRecordMapper {
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

    @Insert("""
            INSERT INTO t_donation_record (donation_id, user_id, amount, channel_id, donor_message, donate_status, created_at)
            VALUES (#{donationId}, #{userId}, #{amount}, #{channelId}, #{donorMessage}, #{donateStatus}, #{createdAt})
            """)
    void insert(DonationRecord record);

    @Update("UPDATE t_donation_record SET donate_status = #{status} WHERE donation_id = #{donationId}")
    int updateStatus(@Param("donationId") String donationId, @Param("status") String status);
}
