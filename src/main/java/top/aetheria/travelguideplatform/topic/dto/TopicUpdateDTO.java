package top.aetheria.travelguideplatform.topic.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class TopicUpdateDTO {

    @NotNull(message = "主题ID不能为空")
    private Long id;

    private String title; // 可选
    private String content; // 可选
}
