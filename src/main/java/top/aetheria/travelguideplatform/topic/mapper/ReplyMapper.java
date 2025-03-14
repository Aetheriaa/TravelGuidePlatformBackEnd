package top.aetheria.travelguideplatform.topic.mapper;

import org.apache.ibatis.annotations.*;
import top.aetheria.travelguideplatform.topic.entity.Reply;

import java.util.List;

@Mapper
public interface ReplyMapper {

    @Insert("INSERT INTO replies (topic_id, user_id, content, create_time, parent_reply_id) " +
            "VALUES (#{topicId}, #{userId}, #{content}, #{createTime}, #{parentReplyId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Reply reply);

    @Select("SELECT * FROM replies WHERE id = #{id}")
    Reply findById(Long id);

    @Select("SELECT * FROM replies WHERE topic_id = #{topicId} ORDER BY create_time ASC")
    List<Reply> findByTopicId(Long topicId);

    @Update("<script>" +            "UPDATE replies " +
            "<set>" +
            "  <if test='content != null'>content = #{content},</if>" +
            "</set>" +
            "WHERE id = #{id}" +
            "</script>")
    void update(Reply reply); //可能需要修改

    @Delete("DELETE FROM replies WHERE id = #{id}")
    void delete(Long id);
    // 根据topicId删除回复
    @Delete("DELETE FROM replies WHERE topic_id = #{topicId}")
    void deleteByTopicId(Long topicId);
}
