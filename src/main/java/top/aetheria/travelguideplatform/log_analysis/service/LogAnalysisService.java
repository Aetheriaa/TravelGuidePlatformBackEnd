package top.aetheria.travelguideplatform.log_analysis.service;

import java.util.List;
import java.util.Map;

public interface LogAnalysisService {
    List<Map<String, Object>> getUserGrowthData(String startDate, String endDate);
    List<Map<String, Object>> getGuidePublishData(String startDate, String endDate);
    List<Map<String, Object>> getItineraryCreateData(String startDate, String endDate);
}
