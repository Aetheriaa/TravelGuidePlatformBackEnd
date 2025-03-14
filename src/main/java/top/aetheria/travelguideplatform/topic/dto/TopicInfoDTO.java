package top.aetheria.travelguideplatform.topic.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TopicInfoDTO {
    private Long id;
    private Long userId;
    private String username; // 发帖人用户名
    private String userAvatar; // 发帖人头像
    private String title;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer viewCount;
    private Integer replyCount;
    private Long lastReplyUserId;
    private String lastReplyUsername; // 最后回复人用户名
    private LocalDateTime lastReplyTime;
}
