package top.aetheria.travelguideplatform.guide.mapper;

import top.aetheria.travelguideplatform.guide.entity.Favorite;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FavoriteMapper {
    @Insert("INSERT INTO favorites(guide_id, user_id) values(#{guideId},#{userId})")
    void insert(Favorite favorite);
    @Delete("DELETE FROM favorites WHERE guide_id = #{guideId} AND user_id = #{userId}")
    void delete(Favorite favorite);

    @Select("SELECT count(*) FROM favorites WHERE guide_id = #{guideId} AND user_id = #{userId}")
    int countByGuideIdAndUserId(Favorite favorite);
}
