package top.aetheria.travelguideplatform.guide.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class GuideCreateDTO {
    @NotBlank(message = "攻略标题不能为空")
    @Size(max = 255, message = "攻略标题长度不能超过255个字符")
    private String title;

    @NotBlank(message = "攻略内容不能为空")
    private String content;

    private String coverImage; // 封面图片，可选

//    private String tags;//标签
    private List<String> tags;
}
