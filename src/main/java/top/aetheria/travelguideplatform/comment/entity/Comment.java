package top.aetheria.travelguideplatform.comment.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id;
    private Long guideId;
    private Long userId;
    private String content;
    private LocalDateTime createTime;
    private Long parentCommentId; // 父评论ID
    private Integer status;
}