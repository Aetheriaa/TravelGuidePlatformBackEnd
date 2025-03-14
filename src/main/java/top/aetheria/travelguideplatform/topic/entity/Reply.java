package top.aetheria.travelguideplatform.topic.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Reply {
    private Long id;
    private Long topicId;
    private Long userId;
    private String content;
    private LocalDateTime createTime;
    private Long parentReplyId;
}