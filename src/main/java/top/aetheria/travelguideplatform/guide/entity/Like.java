package top.aetheria.travelguideplatform.guide.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Like {
    private Long id;
    private Long guideId;
    private Long userId;
    private LocalDateTime createTime;
}
