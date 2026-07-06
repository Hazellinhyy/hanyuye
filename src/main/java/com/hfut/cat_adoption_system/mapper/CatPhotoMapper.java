package com.hfut.cat_adoption_system.mapper;

import com.hfut.cat_adoption_system.model.CatPhoto;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 猫咪照片Mapper接口
 * 
 * 提供猫咪照片的增删改查操作，支持照片管理和人脸识别特征向量存储。
 */
@Mapper
public interface CatPhotoMapper {

        /**
         * 根据猫咪ID查询照片列表
         * 
         * 按封面标记优先、识别权重降序、上传时间升序排列，确保封面照片优先显示。
         * 
         * @param catId 猫咪ID
         * @return 该猫咪的照片列表
         */
        @ConstructorArgs({
                        @Arg(column = "photo_id", javaType = String.class),
                        @Arg(column = "cat_id", javaType = String.class),
                        @Arg(column = "photo_url", javaType = String.class),
                        @Arg(column = "angle_code", javaType = String.class),
                        @Arg(column = "photo_scene", javaType = String.class),
                        @Arg(column = "is_cover", javaType = boolean.class),
                        @Arg(column = "recognition_weight", javaType = java.math.BigDecimal.class),
                        @Arg(column = "feature_vector", javaType = String.class),
                        @Arg(column = "feature_note", javaType = String.class),
                        @Arg(column = "uploaded_at", javaType = java.time.LocalDateTime.class)
        })
        @Select("""
                        SELECT photo_id, cat_id, photo_url, angle_code, photo_scene, is_cover,
                               recognition_weight, feature_vector, feature_note, uploaded_at
                        FROM t_cat_photo
                        WHERE cat_id = #{catId}
                        ORDER BY is_cover DESC, recognition_weight DESC, uploaded_at ASC
                        """)
        List<CatPhoto> findByCatId(String catId);

        /**
         * 查询所有猫咪照片
         * 
         * 按猫咪ID升序、封面标记优先、拍摄角度编码升序排列。
         * 
         * @return 所有猫咪照片列表
         */
        @ConstructorArgs({
                        @Arg(column = "photo_id", javaType = String.class),
                        @Arg(column = "cat_id", javaType = String.class),
                        @Arg(column = "photo_url", javaType = String.class),
                        @Arg(column = "angle_code", javaType = String.class),
                        @Arg(column = "photo_scene", javaType = String.class),
                        @Arg(column = "is_cover", javaType = boolean.class),
                        @Arg(column = "recognition_weight", javaType = java.math.BigDecimal.class),
                        @Arg(column = "feature_vector", javaType = String.class),
                        @Arg(column = "feature_note", javaType = String.class),
                        @Arg(column = "uploaded_at", javaType = java.time.LocalDateTime.class)
        })
        @Select("""
                        SELECT photo_id, cat_id, photo_url, angle_code, photo_scene, is_cover,
                               recognition_weight, feature_vector, feature_note, uploaded_at
                        FROM t_cat_photo
                        ORDER BY cat_id ASC, is_cover DESC, angle_code ASC
                        """)
        List<CatPhoto> findAll();

        /**
         * 插入猫咪照片记录
         * 
         * @param photo 照片信息对象（包含特征向量、拍摄角度等信息）
         */
        @Insert("""
                        INSERT INTO t_cat_photo (photo_id, cat_id, photo_url, angle_code, photo_scene, is_cover,
                                                 recognition_weight, feature_vector, feature_note, uploaded_at)
                        VALUES (#{photoId}, #{catId}, #{photoUrl}, #{angleCode}, #{photoScene}, #{cover},
                                #{recognitionWeight}, #{featureVector}, #{featureNote}, #{uploadedAt})
                        """)
        void insert(CatPhoto photo);

        /**
         * 更新照片的人脸识别特征向量
         * 
         * 用于猫咪人脸识别功能，将提取的特征向量存储到数据库。
         * 
         * @param photoId       照片ID
         * @param featureVector 人脸识别特征向量（JSON格式）
         */
        @Update("UPDATE t_cat_photo SET feature_vector = #{featureVector} WHERE photo_id = #{photoId}")
        void updateFeatureVector(@Param("photoId") String photoId, @Param("featureVector") String featureVector);

        /**
         * 删除单张照片
         * 
         * @param photoId 照片ID
         * @return 删除的记录数
         */
        @Delete("DELETE FROM t_cat_photo WHERE photo_id = #{photoId}")
        int delete(String photoId);

        /**
         * 删除某猫咪的所有照片
         * 
         * 用于猫咪删除时级联删除照片记录。
         * 
         * @param catId 猫咪ID
         * @return 删除的记录数
         */
        @Delete("DELETE FROM t_cat_photo WHERE cat_id = #{catId}")
        int deleteByCatId(String catId);
}