package top.aetheria.travelguideplatform.comment.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentInfoDTO {
    private Long id;
    private Long guideId;
    private Long userId;
    private String username; // 评论者用户名
    private String userAvatar; // 评论者头像
    private String content;
    private LocalDateTime createTime;
    private Long parentCommentId; //回复的评论的id
    private String parentCommentUserName;  // 被回复的评论的用户名
    private List<CommentInfoDTO> replies; // 子评论（回复）列表
}