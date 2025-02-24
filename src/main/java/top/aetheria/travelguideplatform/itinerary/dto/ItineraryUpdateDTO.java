package top.aetheria.travelguideplatform.itinerary.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
public class ItineraryUpdateDTO {
    @NotNull
    private  Long id;

    @NotBlank(message = "行程名称不能为空")
    private String name;

    @NotNull(message = "开始日期不能为空")
    private Date startDate;

    @NotNull(message = "结束日期不能为空")
    private Date endDate;

    private String description;

    private List<ItineraryDetailCreateDTO> details;
}