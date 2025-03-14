package top.aetheria.travelguideplatform.topic.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ReplyCreateDTO {

    @NotNull(message = "主题ID不能为空")
    private Long topicId;

    @NotBlank(message = "回复内容不能为空")
    private String content;

    private Long parentReplyId; // 可选，用于回复的回复
}