package com.hfut.cat_adoption_system.mapper;

import com.hfut.cat_adoption_system.model.DonationChannel;
import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.annotations.ConstructorArgs;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 捐赠渠道Mapper接口
 * 
 * 提供捐赠渠道信息的查询操作，用于获取可用的捐赠二维码信息。
 */
@Mapper
public interface DonationChannelMapper {

        /**
         * 查询启用的捐赠渠道
         * 
         * 返回最新更新的启用状态捐赠渠道，用于展示捐赠二维码给用户。
         * 
         * @return 启用的捐赠渠道信息（包含渠道ID、名称、二维码URL等）
         */
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