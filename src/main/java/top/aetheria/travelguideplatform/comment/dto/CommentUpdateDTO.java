package top.aetheria.travelguideplatform.comment.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CommentUpdateDTO {
    @NotNull
    private Long id;
    @NotBlank
    private String content;
}
