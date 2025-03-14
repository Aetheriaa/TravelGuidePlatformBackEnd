package top.aetheria.travelguideplatform.topic.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReplyInfoDTO {
    private Long id;
    private Long topicId;
    private Long userId;
    private String username; // 回复者用户名
    private String userAvatar;
    private String content;
    private LocalDateTime createTime;
    private Long parentReplyId; // 可选
    private String parentReplyUsername;//被回复者的用户名
}
