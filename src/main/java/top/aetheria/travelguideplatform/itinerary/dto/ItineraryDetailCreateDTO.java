package top.aetheria.travelguideplatform.itinerary.dto;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

@Data
public class ItineraryDetailCreateDTO {
    @NotBlank(message = "行程详情类型不能为空")
    private String type; // 使用 String 类型，对应 ENUM('attraction', 'hotel', 'transport', 'restaurant')

    @NotNull(message = "关联ID不能为空")
    private Long itemId;

    @NotNull(message = "天数不能为空")
    private Integer day;

    private LocalTime startTime; // 可选
    private LocalTime endTime; // 可选
    private String notes; // 可选
}