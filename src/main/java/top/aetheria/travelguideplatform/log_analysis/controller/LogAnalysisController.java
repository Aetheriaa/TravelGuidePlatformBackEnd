package top.aetheria.travelguideplatform.log_analysis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.aetheria.travelguideplatform.common.vo.Result;
import top.aetheria.travelguideplatform.log_analysis.service.LogAnalysisService; // 假设你创建了这个 Service

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/analysis") // 定义分析相关的 API 路径
public class LogAnalysisController {

    @Autowired
    private LogAnalysisService logAnalysisService;

    @GetMapping("/user-growth")
    public Result<List<Map<String, Object>>> getUserGrowth(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        List<Map<String, Object>> data = logAnalysisService.getUserGrowthData(startDate, endDate);
        return Result.success(data);
    }

    @GetMapping("/guide-publish")
    public Result<List<Map<String, Object>>> getGuidePublish(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        List<Map<String, Object>> data = logAnalysisService.getGuidePublishData(startDate, endDate);
        return Result.success(data);
    }

    @GetMapping("/itinerary-create")
    public Result<List<Map<String, Object>>> getItineraryCreate(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        List<Map<String, Object>> data = logAnalysisService.getItineraryCreateData(startDate, endDate);
        return Result.success(data);
    }
}