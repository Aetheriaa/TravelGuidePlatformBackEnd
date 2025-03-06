package top.aetheria.travelguideplatform.private_message.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PrivateMessageDTO {
    private Long id;
    private Long senderId;
    private String senderName; // 发送者用户名
    private String senderAvatar;
    private Long receiverId;
    private String receiverName; // 接收者用户名
    private String receiverAvatar;
    private String content;
    private LocalDateTime sendTime;
    private Boolean isRead;
    // 可以根据需要添加其他字段，例如 senderAvatar, receiverAvatar
}
