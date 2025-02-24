package top.aetheria.travelguideplatform.itinerary.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class ItineraryInfoDTO {
    private Long id;
    private Long userId;
    private String username;
    private String name;
    private Date startDate;
    private Date endDate;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<ItineraryDetailDTO> details; // 行程详情列表
}