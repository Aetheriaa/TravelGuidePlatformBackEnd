package top.aetheria.travelguideplatform.comment.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CommentCreateDTO {
    @NotNull(message = "攻略ID不能为空")
    private Long guideId;

    @NotBlank(message = "评论内容不能为空")
    private String content;

    private Long parentCommentId; // 回复评论时，指定父评论ID，可选
}
