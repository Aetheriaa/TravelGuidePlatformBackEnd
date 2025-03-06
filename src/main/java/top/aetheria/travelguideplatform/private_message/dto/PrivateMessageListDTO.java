package top.aetheria.travelguideplatform.private_message.dto;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class PrivateMessageListDTO {
    private Long id;
    private Long otherUserId;
    private String otherUsername; // 发送者用户名
    private String otherUserAvatar;
    private String content;
    private LocalDateTime sendTime;
    private Boolean isRead;
}
