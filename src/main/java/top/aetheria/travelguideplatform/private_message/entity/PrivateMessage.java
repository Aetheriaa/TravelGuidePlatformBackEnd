package top.aetheria.travelguideplatform.private_message.entity;

import lombok.Data;
import java.time.LocalDateTime;
@Data
public class PrivateMessage {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String content;
    private LocalDateTime sendTime;
    private Boolean isRead; // 是否已读
}
