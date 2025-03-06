package top.aetheria.travelguideplatform.guide.mapper;

import top.aetheria.travelguideplatform.guide.entity.Guide;
import top.aetheria.travelguideplatform.guide.entity.Like;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LikeMapper {
    @Insert("INSERT INTO likes(guide_id, user_id) values(#{guideId},#{userId})")
    void insert(Like like);

    @Delete("DELETE FROM likes WHERE guide_id = #{guideId} AND user_id = #{userId}")
    void delete(Like like);

    @Select("SELECT COUNT(*) FROM likes WHERE guide_id = #{guideId} AND user_id = #{userId}")
    int countByGuideIdAndUserId(Like like);

    @Select("SELECT COUNT(*) FROM likes WHERE guide_id = #{guideId}")
    int countByGuideId(Long guideId); //用于更新攻略表中的点赞数

    //    // 查询用户点赞的攻略ID列表
//    @Select("SELECT guide_id FROM likes WHERE user_id = #{userId} ORDER BY create_time DESC")
//    List<Long> findLikedGuideIdsByUserId(Long userId);
//直接查询出攻略
    @Select("SELECT g.* FROM guides g JOIN likes l ON g.id = l.guide_id WHERE l.user_id = #{userId} ORDER BY l.create_time DESC")
    List<Guide> findLikedGuideIdsByUserId(Long userId);
}
