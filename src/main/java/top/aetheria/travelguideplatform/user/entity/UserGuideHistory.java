package top.aetheria.travelguideplatform.user.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserGuideHistory {
    private Long id;
    private Long userId;
    private Long guideId;
    private LocalDateTime viewTime;

    // 构造函数
    public UserGuideHistory(Long userId, Long guideId, LocalDateTime viewTime) {
        this.userId = userId;
        this.guideId = guideId;
        this.viewTime = viewTime;
    }
}
