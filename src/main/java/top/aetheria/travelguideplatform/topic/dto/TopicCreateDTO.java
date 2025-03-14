package top.aetheria.travelguideplatform.topic.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class TopicCreateDTO {

    @NotBlank(message = "主题标题不能为空")
    private String title;

    @NotBlank(message = "主题内容不能为空")
    private String content;
}
