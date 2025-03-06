package top.aetheria.travelguideplatform.private_message.service;

import top.aetheria.travelguideplatform.private_message.dto.PrivateMessageCreateDTO;
import top.aetheria.travelguideplatform.private_message.dto.PrivateMessageDTO;
import top.aetheria.travelguideplatform.private_message.dto.PrivateMessageListDTO;

import java.util.List;

public interface PrivateMessageService {
    PrivateMessageDTO sendMessage(Long senderId, PrivateMessageCreateDTO createDTO);
    List<PrivateMessageDTO> getConversation(Long userId1, Long userId2);
    List<PrivateMessageListDTO> getRecentContacts(Long userId); // 获取最近联系人列表
    void markAsRead(Long messageId);
    void deleteMessage(Long messageId, Long userId); // userId 用于权限检查
}