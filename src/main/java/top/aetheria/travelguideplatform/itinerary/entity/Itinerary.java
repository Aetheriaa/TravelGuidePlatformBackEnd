package top.aetheria.travelguideplatform.itinerary.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class Itinerary {
    private Long id;
    private Long userId;
    private String name;
    private Date startDate;
    private Date endDate;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
