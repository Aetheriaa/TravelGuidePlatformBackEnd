package top.aetheria.travelguideplatform.itinerary.mapper;

import org.apache.ibatis.annotations.*;
import top.aetheria.travelguideplatform.itinerary.dto.ItineraryListDTO;
import top.aetheria.travelguideplatform.itinerary.entity.Itinerary;

import java.util.List;

@Mapper
public interface ItineraryMapper {

    @Insert("INSERT INTO itineraries (user_id, name, start_date, end_date, description, create_time, update_time) " +
            "VALUES (#{userId}, #{name}, #{startDate}, #{endDate}, #{description}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Itinerary itinerary);

    @Select("SELECT * FROM itineraries WHERE id = #{id}")
    @Results(id = "ItineraryResultMap", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "name", column = "name"),
            @Result(property = "startDate", column = "start_date"),
            @Result(property = "endDate", column = "end_date"),
            @Result(property = "description", column = "description"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time")
    })
    Itinerary findById(Long id);
    @Select("SELECT * FROM itineraries WHERE user_id = #{userId}")
    List<Itinerary> findByUserId(Long userId);

    @Update("UPDATE itineraries SET name = #{name}, start_date = #{startDate}, end_date = #{endDate}, " +
            "description = #{description}, update_time = #{updateTime} WHERE id = #{id}")
    void update(Itinerary itinerary);

    @Delete("DELETE FROM itineraries WHERE id = #{id}")
    void delete(Long id);

    // 列表查询（支持分页、关键词搜索）
    List<Itinerary> list(ItineraryListDTO itineraryListDTO);
    Long count(ItineraryListDTO itineraryListDTO);
}