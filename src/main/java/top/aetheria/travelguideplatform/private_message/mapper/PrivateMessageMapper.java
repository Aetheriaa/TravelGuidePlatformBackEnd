package top.aetheria.travelguideplatform.private_message.mapper;

import org.apache.ibatis.annotations.*;
import top.aetheria.travelguideplatform.private_message.dto.PrivateMessageDTO;
import top.aetheria.travelguideplatform.private_message.dto.PrivateMessageListDTO;
import top.aetheria.travelguideplatform.private_message.entity.PrivateMessage;

import java.util.List;

@Mapper
public interface PrivateMessageMapper {

    @Insert("INSERT INTO private_messages (sender_id, receiver_id, content, send_time, is_read) " +
            "VALUES (#{senderId}, #{receiverId}, #{content}, #{sendTime}, #{isRead})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(PrivateMessage message);

    @Select("SELECT * FROM private_messages WHERE id = #{id}")
    PrivateMessage findById(Long id);

    // 查询与某个用户的私信对话 (按时间排序)
    @Select("SELECT pm.*, s.username AS senderName, r.username AS receiverName " +
            "FROM private_messages pm " +
            "JOIN users s ON pm.sender_id = s.id " +
            "JOIN users r ON pm.receiver_id = r.id " +
            "WHERE (pm.sender_id = #{userId1} AND pm.receiver_id = #{userId2}) " +
            "   OR (pm.sender_id = #{userId2} AND pm.receiver_id = #{userId1}) " +
            "ORDER BY pm.send_time")
    List<PrivateMessageDTO> findConversation(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
    //这里返回的是DTO

    // 查询私信列表 (最近的联系人)
//    @Select("SELECT other_user.id AS otherUserId, other_user.username AS otherUsername, " +
//            "       pm.content, pm.send_time, pm.is_read " +
//            "FROM ( " +
//            "    SELECT " +
//            "        CASE " +
//            "            WHEN sender_id = #{userId} THEN receiver_id " +
//            "            ELSE sender_id " +
//            "        END AS other_user_id, " +
//            "        MAX(send_time) AS max_send_time " + // 取每个对话的最新一条消息的时间
//            "    FROM private_messages " +
//            "    WHERE sender_id = #{userId} OR receiver_id = #{userId} " +
//            "    GROUP BY other_user_id " +
//            ") AS latest_messages " +
//            "JOIN private_messages pm ON ( " +
//            "    (pm.sender_id = #{userId} AND pm.receiver_id = latest_messages.other_user_id) " +
//            "    OR (pm.sender_id = latest_messages.other_user_id AND pm.receiver_id = #{userId}) " +
//            ") " +
//            "JOIN users other_user ON other_user.id = latest_messages.other_user_id " +
//            "WHERE pm.send_time = latest_messages.max_send_time " + // 只取每个对话的最新一条消息
//            "ORDER BY pm.send_time DESC")
//    @Select("SELECT other_user.id AS otherUserId, other_user.username AS otherUsername," +
//            "         pm.content, pm.send_time, pm.is_read" +
//            "  FROM (" +
//            "    SELECT" +
//            "        CASE" +
//            "            WHEN sender_id = #{userId} THEN receiver_id" +
//            "            ELSE sender_id" +
//            "        END AS other_user_id," +
//            "        MAX(send_time) AS max_send_time  " +
//            "    FROM private_messages" +
//            "    WHERE sender_id = #{userId} OR receiver_id = #{userId}" +
//            "    GROUP BY other_user_id" +
//            "  ) AS latest_messages" +
//            "  JOIN private_messages pm ON (" +
//            "    (pm.sender_id = #{userId} AND pm.receiver_id = latest_messages.other_user_id)\n" +
//            "    OR (pm.sender_id = latest_messages.other_user_id AND pm.receiver_id = #{userId})\n" +
//            "  )" +
//            "  JOIN users other_user ON other_user.id = latest_messages.other_user_id\n" +
//            "  WHERE pm.send_time = latest_messages.max_send_time  <!-- 只取每个对话的最新一条消息 -->\n" +
//            "  ORDER BY pm.send_time DESC")
    @Select("SELECT other_user.id AS otherUserId, other_user.username AS otherUsername,other_user.avatar as otherUserAvatar, " +
            "       pm.content, pm.send_time, pm.is_read " +
            "FROM ( " +
            "    SELECT " +
            "        CASE " +
            "            WHEN sender_id = #{userId} THEN receiver_id " +
            "            ELSE sender_id " +
            "        END AS other_user_id, " +
            "        MAX(send_time) AS max_send_time " + // 取每个对话的最新一条消息的时间
            "    FROM private_messages " +
            "    WHERE sender_id = #{userId} OR receiver_id = #{userId} " +
            "    GROUP BY other_user_id " +
            ") AS latest_messages " +
            "JOIN private_messages pm ON ( " +
            "    (pm.sender_id = #{userId} AND pm.receiver_id = latest_messages.other_user_id) " +
            "    OR (pm.sender_id = latest_messages.other_user_id AND pm.receiver_id = #{userId}) " +
            ") " +
            "JOIN users other_user ON other_user.id = latest_messages.other_user_id " +
            "WHERE pm.send_time = latest_messages.max_send_time " + // 只取每个对话的最新一条消息
            "ORDER BY pm.send_time DESC")
    List<PrivateMessageListDTO> findRecentContacts(@Param("userId") Long userId);

    @Update("UPDATE private_messages SET is_read = 1 WHERE id = #{id}")
    void markAsRead(Long id);

    @Delete("DELETE FROM private_messages WHERE id = #{id}")
    void delete(Long id);
}
