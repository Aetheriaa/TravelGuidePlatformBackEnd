package top.aetheria.travelguideplatform.guide.mapper;

import org.apache.ibatis.annotations.*;
import top.aetheria.travelguideplatform.guide.dto.GuideListDTO;
import top.aetheria.travelguideplatform.guide.entity.Guide;
import top.aetheria.travelguideplatform.guide.entity.Tag;

import java.util.List;

@Mapper
public interface GuideMapper {

    @Insert("INSERT INTO guides(user_id, title, content, cover_image, create_time, update_time, status,tags) " +
            "VALUES(#{userId}, #{title}, #{content}, #{coverImage}, #{createTime}, #{updateTime}, #{status},#{tags})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Guide guide);

    @Select("SELECT * FROM guides WHERE id = #{id} AND status != -1") // 查询时排除已删除的攻略
    Guide findById(Long id);

    @Update("UPDATE guides SET title = #{title}, content = #{content}, cover_image = #{coverImage}, update_time = #{updateTime},tags=#{tags} " +
            "WHERE id = #{id}")
    void update(Guide guide);

    @Update("UPDATE guides SET status = -1 WHERE id = #{id}") // 逻辑删除
    void delete(Long id);
    @Select("UPDATE guides SET status = #{status} WHERE id = #{id}")
    void updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Select("UPDATE guides set view_count = view_count+1 where id = #{id}")
    void incrementViewCount(Long id); // 增加阅读量

    // 列表查询（支持分页、关键词搜索、标签筛选、排序）
    List<Guide> list(GuideListDTO guideListDTO);

    //    @Select("SELECT COUNT(*) FROM guides WHERE status = 1")  // 只统计已发布的攻略数量
    Long count(GuideListDTO guideListDTO);

    @Select("SELECT * FROM guides WHERE id = #{id} and status = #{status} ")
    Guide findByIdAndStatus(@Param("id") Long id, @Param("status") Integer status);

    @Insert("<script>" +
            "INSERT INTO guide_tags (guide_id, tag_id) VALUES " +
            "<foreach collection='tags' item='tag' separator=','>" +
            "(#{guideId}, #{tag.id})" +
            "</foreach>" +
            "</script>")
    void insertGuideTags(@Param("guideId") Long guideId, @Param("tags") List<Tag> tags);

    // 查询攻略的所有标签
    @Select("SELECT t.* FROM tags t JOIN guide_tags gt ON t.id = gt.tag_id WHERE gt.guide_id = #{guideId}")
    List<Tag> findTagsByGuideId(Long guideId);

    // 删除攻略的所有标签关联
    @Delete("DELETE FROM guide_tags WHERE guide_id = #{guideId}")
    void deleteGuideTags(Long guideId);

    // 根据ID列表查询攻略
    @Select("<script>" +
            "SELECT * FROM guides WHERE id IN " +
            "<foreach item='item' index='index' collection='guideIds' open='(' separator=',' close=')'>" +
            "#{item}" +
            "</foreach>" +
            "</script>")
    List<Guide> findByIds(@Param("guideIds") List<Long> guideIds);

}