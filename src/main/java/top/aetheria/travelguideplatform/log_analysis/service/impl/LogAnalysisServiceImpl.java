package top.aetheria.travelguideplatform.log_analysis.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.aetheria.travelguideplatform.log_analysis.mapper.LogAnalysisMapper; // 假设你创建了这个 Mapper
import top.aetheria.travelguideplatform.log_analysis.service.LogAnalysisService;

import java.util.List;
import java.util.Map;

@Service
public class LogAnalysisServiceImpl implements LogAnalysisService {

    @Autowired
    private LogAnalysisMapper logAnalysisMapper;

    @Override
    public List<Map<String, Object>> getUserGrowthData(String startDate, String endDate) {
        return logAnalysisMapper.getUserGrowthStats(startDate, endDate);
    }

    @Override
    public List<Map<String, Object>> getGuidePublishData(String startDate, String endDate) {
        return logAnalysisMapper.getGuidePublishStats(startDate, endDate);
    }

    @Override
    public List<Map<String, Object>> getItineraryCreateData(String startDate, String endDate) {
        return logAnalysisMapper.getItineraryCreateStats(startDate, endDate);
    }
}
