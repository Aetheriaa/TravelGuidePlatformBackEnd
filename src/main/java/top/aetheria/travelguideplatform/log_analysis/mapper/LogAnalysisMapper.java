package top.aetheria.travelguideplatform.log_analysis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface LogAnalysisMapper {

    @Select("<script>" +
            "SELECT date, new_users FROM user_growth_stats " +
            "<where>" +
            "  <if test='startDate != null and startDate != \"\"'>" +
            "    AND date >= #{startDate}" +
            "  </if>" +
            "  <if test='endDate != null and endDate != \"\"'>" +
            "    AND date &lt;= #{endDate}" + // <---- 修改在这里
            "  </if>" +
            "</where>" +
            "ORDER BY date ASC" +
            "</script>")
    List<Map<String, Object>> getUserGrowthStats(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Select("<script>" +
            "SELECT date, new_guides FROM guide_publish_stats " +
            "<where>" +
            "  <if test='startDate != null and startDate != \"\"'>" +
            "    AND date >= #{startDate}" +
            "  </if>" +
            "  <if test='endDate != null and endDate != \"\"'>" +
            "    AND date &lt;= #{endDate}" + // <---- 修改在这里
            "  </if>" +
            "</where>" +
            "ORDER BY date ASC" +
            "</script>")
    List<Map<String, Object>> getGuidePublishStats(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Select("<script>" +
            "SELECT date, new_itineraries FROM itinerary_create_stats " +
            "<where>" +
            "  <if test='startDate != null and startDate != \"\"'>" +
            "    AND date >= #{startDate}" +
            "  </if>" +
            "  <if test='endDate != null and endDate != \"\"'>" +
            "    AND date &lt;= #{endDate}" + // <---- 修改在这里
            "  </if>" +
            "</where>" +
            "ORDER BY date ASC" +
            "</script>")
    List<Map<String, Object>> getItineraryCreateStats(@Param("startDate") String startDate, @Param("endDate") String endDate);
}