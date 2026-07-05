package com.hfut.cat_adoption_system.mapper;

import com.hfut.cat_adoption_system.model.CatPhoto;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CatPhotoMapper {
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

    @Insert("""
            INSERT INTO t_cat_photo (photo_id, cat_id, photo_url, angle_code, photo_scene, is_cover,
                                     recognition_weight, feature_vector, feature_note, uploaded_at)
            VALUES (#{photoId}, #{catId}, #{photoUrl}, #{angleCode}, #{photoScene}, #{cover},
                    #{recognitionWeight}, #{featureVector}, #{featureNote}, #{uploadedAt})
            """)
    void insert(CatPhoto photo);

    @Update("UPDATE t_cat_photo SET feature_vector = #{featureVector} WHERE photo_id = #{photoId}")
    void updateFeatureVector(@Param("photoId") String photoId, @Param("featureVector") String featureVector);

    @Delete("DELETE FROM t_cat_photo WHERE photo_id = #{photoId}")
    int delete(String photoId);

    @Delete("DELETE FROM t_cat_photo WHERE cat_id = #{catId}")
    int deleteByCatId(String catId);
}
