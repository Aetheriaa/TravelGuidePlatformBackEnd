package top.aetheria.travelguideplatform.guide.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class GuideUpdateDTO {
    @NotNull(message = "攻略ID不能为空")
    private Long id;

    @NotBlank(message = "攻略标题不能为空")
    @Size(max = 255, message = "攻略标题长度不能超过255个字符")
    private String title;

    @NotBlank(message = "攻略内容不能为空")
    private String content;

    private String coverImage; // 封面图片，可选

//    private String tags;
    private List<String> tags;
}
