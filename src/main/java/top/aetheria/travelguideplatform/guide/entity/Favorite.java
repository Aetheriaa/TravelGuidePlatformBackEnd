package top.aetheria.travelguideplatform.guide.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Favorite {
    private Long id;
    private Long guideId;
    private Long userId;
    private LocalDateTime createTime;
}
