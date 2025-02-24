package top.aetheria.travelguideplatform.itinerary.dto;

import lombok.Data;

@Data
public class ItineraryListDTO {
    private String keyword; //搜索
    private Integer page = 1;       // 当前页码，默认第1页
    private Integer pageSize = 10;   // 每页大小，默认10条
}