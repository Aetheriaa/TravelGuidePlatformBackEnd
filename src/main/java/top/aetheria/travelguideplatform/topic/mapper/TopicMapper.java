package top.aetheria.travelguideplatform.topic.mapper;

import org.apache.ibatis.annotations.*;
import top.aetheria.travelguideplatform.topic.dto.TopicListDTO;
import top.aetheria.travelguideplatform.topic.entity.Topic;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TopicMapper {

    @Insert("INSERT INTO topics (user_id, title, content, create_time, update_time) " +
            "VALUES (#{userId}, #{title}, #{content}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Topic topic);

    @Select("SELECT * FROM topics WHERE id = #{id}")
    Topic findById(Long id);

    @Update("<script>" +
            "UPDATE topics " +
            "<set>" +
            "  <if test='title != null'>title = #{title},</if>" +
            "  <if test='content != null'>content = #{content},</if>" +
            "  update_time = #{updateTime} " +
            "</set>" +
            "WHERE id = #{id}" +
            "</script>")
    void update(Topic topic);

    @Delete("DELETE FROM topics WHERE id = #{id}")
    void delete(Long id);
    // 增加浏览次数
    @Update("UPDATE topics SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementViewCount(Long id);

    // 更新最后回复信息
    @Update("UPDATE topics SET last_reply_user_id = #{userId}, last_reply_time = #{replyTime}, reply_count = reply_count + 1 WHERE id = #{topicId}")
    void updateLastReply(@Param("topicId") Long topicId, @Param("userId") Long userId, @Param("replyTime") LocalDateTime replyTime);

    // 列表查询（支持分页、关键词搜索、排序）
    List<Topic> list(TopicListDTO topicListDTO);
    Long count(TopicListDTO topicListDTO);

}