package top.aetheria.travelguideplatform.comment.mapper;

import org.apache.ibatis.annotations.*;
import top.aetheria.travelguideplatform.comment.entity.Comment;

import java.util.List;

@Mapper
public interface CommentMapper {

    @Insert("INSERT INTO comments (guide_id, user_id, content, create_time, parent_comment_id,status) " +
            "VALUES (#{guideId}, #{userId}, #{content}, #{createTime}, #{parentCommentId},#{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Comment comment);

    @Select("SELECT * FROM comments WHERE id = #{id} and status = 1")
    Comment findById(Long id);

    // 查询某个攻略下的所有一级评论（parent_comment_id 为 null 的评论）
    @Select("SELECT * FROM comments WHERE guide_id = #{guideId} AND parent_comment_id IS NULL AND status = 1 ORDER BY create_time DESC")
    List<Comment> findByGuideId(Long guideId);

    // 查询某个评论的所有子评论（回复）
    @Select("SELECT * FROM comments WHERE parent_comment_id = #{parentCommentId} and status = 1 ORDER BY create_time ASC")
    List<Comment> findRepliesByParentId(Long parentCommentId);

    @Update("UPDATE comments SET content = #{content} WHERE id = #{id}")
    void update(Comment comment);
    @Update("UPDATE comments SET status = 0 WHERE id = #{id}")
    void delete(Long id); //逻辑删除

    @Select("SELECT * from comments where guide_id = #{guideId} and status = 1 order by create_time desc")
    List<Comment> findAllByGuideId(Long guideId);
}

