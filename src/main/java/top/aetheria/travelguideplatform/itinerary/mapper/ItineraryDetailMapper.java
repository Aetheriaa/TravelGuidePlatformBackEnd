package top.aetheria.travelguideplatform.itinerary.mapper;

import org.apache.ibatis.annotations.*;
import top.aetheria.travelguideplatform.itinerary.entity.ItineraryDetail;

import java.util.List;

@Mapper
public interface ItineraryDetailMapper {

    @Insert("INSERT INTO itinerary_details (itinerary_id, type, item_id, day, start_time, end_time, notes) " +
            "VALUES (#{itineraryId}, #{type}, #{itemId}, #{day}, #{startTime}, #{endTime}, #{notes})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(ItineraryDetail detail);

    @Select("SELECT * FROM itinerary_details WHERE id = #{id}")
    ItineraryDetail findById(Long id);

    @Select("SELECT * FROM itinerary_details WHERE itinerary_id = #{itineraryId} ORDER BY day, start_time")
    List<ItineraryDetail> findByItineraryId(Long itineraryId);

    @Update("<script>" +
            "UPDATE itinerary_details " +
            "<set>" +
            "  <if test='type != null'>type = #{type},</if>" +
            "  <if test='itemId != null'>item_id = #{itemId},</if>" +
            "  <if test='day != null'>day = #{day},</if>" +
            "  <if test='startTime != null'>start_time = #{startTime},</if>" +
            "  <if test='endTime != null'>end_time = #{endTime},</if>" +
            "  <if test='notes != null'>notes = #{notes},</if>" +
            "</set>" +
            "WHERE id = #{id}" +
            "</script>")
    void update(ItineraryDetail detail);

    @Delete("DELETE FROM itinerary_details WHERE id = #{id}")
    void delete(Long id);

    @Delete("DELETE FROM itinerary_details WHERE itinerary_id = #{itineraryId}")
    void deleteByItineraryId(Long itineraryId);
}