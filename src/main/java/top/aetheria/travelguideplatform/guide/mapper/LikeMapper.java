package top.aetheria.travelguideplatform.guide.mapper;

import top.aetheria.travelguideplatform.guide.entity.Like;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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
}
