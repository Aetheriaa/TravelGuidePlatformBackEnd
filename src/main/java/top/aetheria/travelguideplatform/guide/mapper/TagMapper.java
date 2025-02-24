package top.aetheria.travelguideplatform.guide.mapper;

import org.apache.ibatis.annotations.*;
import top.aetheria.travelguideplatform.guide.entity.Tag;

import java.util.List;

@Mapper
public interface TagMapper {

    @Insert("INSERT INTO tags (name) VALUES (#{name}) ON DUPLICATE KEY UPDATE popularity = popularity + 1")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertOrUpdate(Tag tag); //插入或更新

    @Select("SELECT * FROM tags WHERE name = #{name}")
    Tag findByName(String name);

    @Select("SELECT * FROM tags ORDER BY popularity DESC LIMIT #{limit}")
    List<Tag> findPopularTags(int limit);

    // 根据攻略ID查询标签
    @Select("SELECT t.* FROM tags t JOIN guide_tags gt ON t.id = gt.tag_id WHERE gt.guide_id = #{guideId}")
    List<Tag> findByGuideId(Long guideId);


}