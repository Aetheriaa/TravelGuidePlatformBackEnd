package top.aetheria.travelguideplatform.guide.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GuideInfoDTO {
    private Long id;
    private Long userId;
    private String authorName; // 作者用户名
    private String authorAvatar; //作者头像
    private String title;
    private String content;
    private String coverImage;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
//    private  String tags;
    private List<String> tags;
    private Boolean liked;     //当前用户是否点赞了该攻略
    private Boolean favorited; //当前用户是否收藏了该攻略.
}
