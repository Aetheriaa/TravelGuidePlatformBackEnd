package top.aetheria.travelguideplatform.user.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.aetheria.travelguideplatform.user.entity.UserGuideHistory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserGuideHistoryMapper {

    @Insert("INSERT INTO user_guide_history (user_id, guide_id, view_time) " +
            "VALUES (#{userId}, #{guideId}, #{viewTime})")
    void insert(UserGuideHistory history);

    // 查询用户最近浏览的攻略ID列表
    @Select("SELECT guide_id FROM user_guide_history WHERE user_id = #{userId} AND view_time >= #{since} ORDER BY view_time DESC")
    List<Long> findRecentGuideIdsByUserId(@Param("userId") Long userId, @Param("since") LocalDateTime since);
//    @Select("SELECT guide_id FROM user_guide_history WHERE user_id = #{userId} ORDER BY view_time DESC")
//     List<Long> findRecentGuideIdsByUserId(@Param("userId") Long userId);

    // 查询用户最近浏览的攻略ID和浏览时间, 返回List<Map<String, Object>>
    @Select("SELECT guide_id, view_time FROM user_guide_history WHERE user_id = #{userId} AND view_time >= #{since} ORDER BY view_time DESC")
    List<Map<String, Object>> findRecentGuidesWithViewTime(@Param("userId") Long userId, @Param("since") LocalDateTime since);
}