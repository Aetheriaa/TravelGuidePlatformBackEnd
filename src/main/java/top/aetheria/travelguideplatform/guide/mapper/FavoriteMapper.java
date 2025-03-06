package top.aetheria.travelguideplatform.guide.mapper;

import top.aetheria.travelguideplatform.guide.entity.Favorite;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import top.aetheria.travelguideplatform.guide.entity.Guide;

import java.util.List;

@Mapper
public interface FavoriteMapper {
    @Insert("INSERT INTO favorites(guide_id, user_id) values(#{guideId},#{userId})")
    void insert(Favorite favorite);
    @Delete("DELETE FROM favorites WHERE guide_id = #{guideId} AND user_id = #{userId}")
    void delete(Favorite favorite);

    @Select("SELECT count(*) FROM favorites WHERE guide_id = #{guideId} AND user_id = #{userId}")
    int countByGuideIdAndUserId(Favorite favorite);

//    // 查询用户收藏的攻略ID列表
//    @Select("SELECT guide_id FROM favorites WHERE user_id = #{userId} ORDER BY create_time DESC")
//    List<Long> findFavoriteGuideIdsByUserId(Long userId);
// 直接查询出攻略
    @Select("SELECT g.* FROM guides g JOIN favorites f ON g.id = f.guide_id WHERE f.user_id = #{userId} ORDER BY f.create_time DESC")
    List<Guide> findFavoriteGuideIdsByUserId(Long userId);

}
