package com.hfut.cat_adoption_system.mapper;

import com.hfut.cat_adoption_system.model.Article;
import com.hfut.cat_adoption_system.model.Comment;
import com.hfut.cat_adoption_system.model.ForumPost;
import com.hfut.cat_adoption_system.model.GenericCollect;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommunityMapper {
    @ConstructorArgs({
            @Arg(column = "article_id", javaType = Integer.class),
            @Arg(column = "title", javaType = String.class),
            @Arg(column = "type_name", javaType = String.class),
            @Arg(column = "cover_url", javaType = String.class),
            @Arg(column = "summary", javaType = String.class),
            @Arg(column = "content", javaType = String.class),
            @Arg(column = "source", javaType = String.class),
            @Arg(column = "source_url", javaType = String.class),
            @Arg(column = "tags", javaType = String.class),
            @Arg(column = "hits", javaType = Integer.class),
            @Arg(column = "praise_count", javaType = Integer.class),
            @Arg(column = "published", javaType = boolean.class),
            @Arg(column = "pinned", javaType = boolean.class),
            @Arg(column = "created_at", javaType = java.time.LocalDateTime.class),
            @Arg(column = "updated_at", javaType = java.time.LocalDateTime.class)
    })
    @Select("""
            <script>
            SELECT a.article_id, a.title, a.type_name, a.cover_url, a.summary, a.content, a.source, a.source_url,
                   (SELECT GROUP_CONCAT(at.tag_name ORDER BY at.tag_name SEPARATOR ',') FROM article_tag at WHERE at.article_id = a.article_id) AS tags,
                   hits, praise_count, published, pinned, created_at, updated_at
            FROM t_article a
            WHERE 1 = 1
            <if test="publishedOnly">AND published = 1</if>
            <if test="keyword != null and keyword != ''">
                AND (title LIKE CONCAT('%', #{keyword}, '%') OR summary LIKE CONCAT('%', #{keyword}, '%')
                     OR EXISTS (SELECT 1 FROM article_tag kt WHERE kt.article_id = a.article_id AND kt.tag_name LIKE CONCAT('%', #{keyword}, '%')))
            </if>
            ORDER BY pinned DESC, updated_at DESC
            </script>
            """)
    List<Article> findArticles(@Param("publishedOnly") boolean publishedOnly, @Param("keyword") String keyword);

    @Select("""
            SELECT a.article_id, a.title, a.type_name, a.cover_url, a.summary, a.content, a.source, a.source_url,
                   (SELECT GROUP_CONCAT(at.tag_name ORDER BY at.tag_name SEPARATOR ',') FROM article_tag at WHERE at.article_id = a.article_id) AS tags,
                   a.hits, a.praise_count, a.published, a.pinned, a.created_at, a.updated_at
            FROM t_article a WHERE a.article_id = #{articleId}
            """)
    Article findArticleById(Integer articleId);

    @Insert("""
            INSERT INTO t_article (title, type_name, cover_url, summary, content, source, source_url, hits, praise_count, published, pinned, created_at, updated_at)
            VALUES (#{title}, #{typeName}, #{coverUrl}, #{summary}, #{content}, #{source}, #{sourceUrl}, #{hits}, #{praiseCount}, #{published}, #{pinned}, #{createdAt}, #{updatedAt})
            """)
    void insertArticle(Article article);

    @Select("""
            SELECT a.article_id, a.title, a.type_name, a.cover_url, a.summary, a.content, a.source, a.source_url,
                   (SELECT GROUP_CONCAT(at.tag_name ORDER BY at.tag_name SEPARATOR ',') FROM article_tag at WHERE at.article_id = a.article_id) AS tags,
                   a.hits, a.praise_count, a.published, a.pinned, a.created_at, a.updated_at
            FROM t_article a ORDER BY a.article_id DESC LIMIT 1
            """)
    Article findLatestArticle();

    @Update("""
            UPDATE t_article SET title = #{title}, type_name = #{typeName}, cover_url = #{coverUrl},
                summary = #{summary}, content = #{content}, source = #{source}, source_url = #{sourceUrl},
                published = #{published}, pinned = #{pinned}, updated_at = #{updatedAt}
            WHERE article_id = #{articleId}
            """)
    int updateArticle(Article article);

    @Insert("INSERT INTO article_tag (article_id, tag_name) VALUES (#{articleId}, #{tagName})")
    void insertArticleTag(@Param("articleId") Integer articleId, @Param("tagName") String tagName);

    @Delete("DELETE FROM article_tag WHERE article_id = #{articleId}")
    void deleteArticleTags(Integer articleId);

    @Delete("DELETE FROM t_article WHERE article_id = #{articleId}")
    int deleteArticle(Integer articleId);

    @Update("UPDATE t_article SET hits = hits + 1 WHERE article_id = #{articleId}")
    void increaseArticleHits(Integer articleId);

    @ConstructorArgs({
            @Arg(column = "post_id", javaType = Integer.class),
            @Arg(column = "user_id", javaType = String.class),
            @Arg(column = "user_name", javaType = String.class),
            @Arg(column = "type_name", javaType = String.class),
            @Arg(column = "title", javaType = String.class),
            @Arg(column = "content", javaType = String.class),
            @Arg(column = "cover_url", javaType = String.class),
            @Arg(column = "hits", javaType = Integer.class),
            @Arg(column = "praise_count", javaType = Integer.class),
            @Arg(column = "status", javaType = String.class),
            @Arg(column = "created_at", javaType = java.time.LocalDateTime.class),
            @Arg(column = "updated_at", javaType = java.time.LocalDateTime.class)
    })
    @Select("""
            <script>
            SELECT p.post_id, p.user_id, u.user_name, p.type_name, p.title, p.content, p.cover_url,
                   p.hits, p.praise_count, p.status, p.created_at, p.updated_at
            FROM t_forum_post p
            JOIN t_user u ON u.user_id = p.user_id
            WHERE 1 = 1
            <if test="publishedOnly">AND p.status = 'PUBLISHED'</if>
            <if test="keyword != null and keyword != ''">
                AND (p.title LIKE CONCAT('%', #{keyword}, '%') OR p.content LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            ORDER BY p.updated_at DESC
            </script>
            """)
    List<ForumPost> findPosts(@Param("publishedOnly") boolean publishedOnly, @Param("keyword") String keyword);

    @Select("""
            SELECT p.post_id, p.user_id, u.user_name, p.type_name, p.title, p.content, p.cover_url,
                   p.hits, p.praise_count, p.status, p.created_at, p.updated_at
            FROM t_forum_post p JOIN t_user u ON u.user_id = p.user_id
            WHERE p.post_id = #{postId}
            """)
    ForumPost findPostById(Integer postId);

    @Insert("""
            INSERT INTO t_forum_post (user_id, type_name, title, content, cover_url, hits, praise_count, status, created_at, updated_at)
            VALUES (#{userId}, #{typeName}, #{title}, #{content}, #{coverUrl}, #{hits}, #{praiseCount}, #{status}, #{createdAt}, #{updatedAt})
            """)
    void insertPost(ForumPost post);

    @Select("""
            SELECT p.post_id, p.user_id, u.user_name, p.type_name, p.title, p.content, p.cover_url,
                   p.hits, p.praise_count, p.status, p.created_at, p.updated_at
            FROM t_forum_post p JOIN t_user u ON u.user_id = p.user_id
            ORDER BY p.post_id DESC LIMIT 1
            """)
    ForumPost findLatestPost();

    @Update("UPDATE t_forum_post SET status = #{status}, updated_at = CURRENT_TIMESTAMP WHERE post_id = #{postId}")
    int updatePostStatus(@Param("postId") Integer postId, @Param("status") String status);

    @Delete("DELETE FROM t_forum_post WHERE post_id = #{postId}")
    int deletePost(Integer postId);

    @ConstructorArgs({
            @Arg(column = "comment_id", javaType = Integer.class),
            @Arg(column = "user_id", javaType = String.class),
            @Arg(column = "nickname", javaType = String.class),
            @Arg(column = "source_type", javaType = String.class),
            @Arg(column = "source_id", javaType = Integer.class),
            @Arg(column = "reply_to_id", javaType = Integer.class),
            @Arg(column = "content", javaType = String.class),
            @Arg(column = "created_at", javaType = java.time.LocalDateTime.class)
    })
    @Select("""
            SELECT c.comment_id, c.user_id, u.user_name AS nickname, c.source_type, c.source_id,
                   c.reply_to_id, c.content, c.created_at
            FROM t_comment c JOIN t_user u ON u.user_id = c.user_id
            WHERE c.source_type = #{sourceType} AND c.source_id = #{sourceId}
            ORDER BY c.created_at ASC
            """)
    List<Comment> findComments(@Param("sourceType") String sourceType, @Param("sourceId") Integer sourceId);

    @Insert("""
            INSERT INTO t_comment (user_id, source_type, source_id, reply_to_id, content, created_at)
            VALUES (#{userId}, #{sourceType}, #{sourceId}, #{replyToId}, #{content}, #{createdAt})
            """)
    void insertComment(Comment comment);

    @Delete("DELETE FROM t_comment WHERE comment_id = #{commentId}")
    int deleteComment(Integer commentId);

    @ConstructorArgs({
            @Arg(column = "user_id", javaType = String.class),
            @Arg(column = "source_type", javaType = String.class),
            @Arg(column = "source_id", javaType = Integer.class),
            @Arg(column = "title", javaType = String.class),
            @Arg(column = "image_url", javaType = String.class),
            @Arg(column = "created_at", javaType = java.time.LocalDateTime.class)
    })
    @Select("""
            SELECT gc.user_id, gc.source_type, gc.source_id,
                   CASE
                     WHEN gc.source_type = 'ARTICLE' THEN a.title
                     WHEN gc.source_type = 'POST' THEN p.title
                     ELSE CONCAT(gc.source_type, '#', gc.source_id)
                   END AS title,
                   CASE
                     WHEN gc.source_type = 'ARTICLE' THEN a.cover_url
                     WHEN gc.source_type = 'POST' THEN p.cover_url
                     ELSE NULL
                   END AS image_url,
                   gc.created_at
            FROM t_generic_collect gc
            LEFT JOIN t_article a ON gc.source_type = 'ARTICLE' AND a.article_id = gc.source_id
            LEFT JOIN t_forum_post p ON gc.source_type = 'POST' AND p.post_id = gc.source_id
            WHERE gc.user_id = #{userId}
            ORDER BY gc.created_at DESC
            """)
    List<GenericCollect> findCollects(String userId);

    @Insert("""
            INSERT INTO t_generic_collect (user_id, source_type, source_id, created_at)
            VALUES (#{userId}, #{sourceType}, #{sourceId}, #{createdAt})
            """)
    void insertCollect(GenericCollect collect);

    @Delete("DELETE FROM t_generic_collect WHERE user_id = #{userId} AND source_type = #{sourceType} AND source_id = #{sourceId}")
    int deleteCollect(@Param("userId") String userId, @Param("sourceType") String sourceType, @Param("sourceId") Integer sourceId);

    @Select("SELECT COUNT(*) FROM t_generic_collect WHERE user_id = #{userId} AND source_type = #{sourceType} AND source_id = #{sourceId}")
    int countCollect(@Param("userId") String userId, @Param("sourceType") String sourceType, @Param("sourceId") Integer sourceId);
}
