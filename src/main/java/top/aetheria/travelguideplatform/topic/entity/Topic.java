package top.aetheria.travelguideplatform.topic.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Topic {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer viewCount;
    private Integer replyCount;
    private Long lastReplyUserId;
    private LocalDateTime lastReplyTime;
}
