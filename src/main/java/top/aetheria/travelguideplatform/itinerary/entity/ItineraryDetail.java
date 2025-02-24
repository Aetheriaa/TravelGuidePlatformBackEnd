package top.aetheria.travelguideplatform.itinerary.entity;

import lombok.Data;
import java.time.LocalTime;

@Data
public class ItineraryDetail {
    private Long id;
    private Long itineraryId;
    private String type;  // 使用 String 类型，对应 ENUM('attraction', 'hotel', 'transport', 'restaurant')
    private Long itemId;
    private Integer day;
    private LocalTime startTime;
    private LocalTime endTime;
    private String notes;
}