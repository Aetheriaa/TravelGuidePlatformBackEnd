package top.aetheria.travelguideplatform.itinerary.dto;

import lombok.Data;

import java.time.LocalTime;

@Data
public class ItineraryDetailDTO {
    private Long id;
    private Long itineraryId;
    private String type;
    private Long itemId;
    private Integer day;
    private LocalTime startTime;
    private LocalTime endTime;
    private String notes;
    //可以根据需要添加name等信息
}