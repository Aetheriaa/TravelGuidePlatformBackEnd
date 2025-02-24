package top.aetheria.travelguideplatform.guide.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Guide {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String coverImage;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer status;
    private String tags;
}
