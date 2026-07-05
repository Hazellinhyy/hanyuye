package com.hfut.cat_adoption_system.mapper;

import com.hfut.cat_adoption_system.model.DonationChannel;
import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.annotations.ConstructorArgs;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DonationChannelMapper {

    @ConstructorArgs({
            @Arg(column = "channel_id", javaType = String.class),
            @Arg(column = "channel_name", javaType = String.class),
            @Arg(column = "qr_url", javaType = String.class),
            @Arg(column = "description", javaType = String.class),
            @Arg(column = "enabled", javaType = boolean.class),
            @Arg(column = "updated_at", javaType = java.time.LocalDateTime.class)
    })
    @Select("""
            SELECT channel_id, channel_name, qr_url, description, enabled, updated_at
            FROM t_donation_channel
            WHERE enabled = 1
            ORDER BY updated_at DESC
            LIMIT 1
            """)
    DonationChannel findEnabled();
}
