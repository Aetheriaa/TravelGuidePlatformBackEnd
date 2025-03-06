package top.aetheria.travelguideplatform.private_message.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.aetheria.travelguideplatform.common.exception.BusinessException;
import top.aetheria.travelguideplatform.private_message.dto.PrivateMessageCreateDTO;
import top.aetheria.travelguideplatform.private_message.dto.PrivateMessageDTO;
import top.aetheria.travelguideplatform.private_message.dto.PrivateMessageListDTO;
import top.aetheria.travelguideplatform.private_message.entity.PrivateMessage;
import top.aetheria.travelguideplatform.private_message.mapper.PrivateMessageMapper;
import top.aetheria.travelguideplatform.private_message.service.PrivateMessageService;
import top.aetheria.travelguideplatform.user.entity.User;
import top.aetheria.travelguideplatform.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PrivateMessageServiceImpl implements PrivateMessageService {

    @Autowired
    private PrivateMessageMapper messageMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public PrivateMessageDTO sendMessage(Long senderId, PrivateMessageCreateDTO createDTO) {
        // 检查接收者是否存在 (省略)

        PrivateMessage message = new PrivateMessage();
        message.setSenderId(senderId);
        message.setReceiverId(createDTO.getReceiverId());
        message.setContent(createDTO.getContent());
        message.setSendTime(LocalDateTime.now());
        message.setIsRead(false); // 新消息默认未读
        messageMapper.insert(message);

        // 转换为 DTO (包括发送者和接收者的用户名)
        PrivateMessageDTO dto = new PrivateMessageDTO();
        BeanUtils.copyProperties(message, dto);

        User sender = userMapper.findById(senderId);
        User receiver = userMapper.findById(createDTO.getReceiverId());
        if (sender != null) {
            dto.setSenderName(sender.getUsername());
//            dto.setSenderAvatar(sender.getAvatar());
        }
        if (receiver != null) {
            dto.setReceiverName(receiver.getUsername());
//            dto.setReceiverAvatar(receiver.getAvatar());
        }
        return dto;
    }

    @Override
    public List<PrivateMessageDTO> getConversation(Long userId1, Long userId2) {

        User user1 = userMapper.findById(userId1);
        User user2 = userMapper.findById(userId2);
        List<PrivateMessageDTO> messageDTOS =  messageMapper.findConversation(userId1, userId2);
        for(var message : messageDTOS) {
            if(message.getSenderId() == userId1){
                message.setSenderAvatar(user1.getAvatar());
                message.setReceiverAvatar(user2.getAvatar());
            }
            else {
                message.setSenderAvatar(user2.getAvatar());
                message.setReceiverAvatar(user1.getAvatar());
            }
        }
        return messageDTOS;
    }

    @Override
    public  List<PrivateMessageListDTO> getRecentContacts(Long userId){
        List<PrivateMessageListDTO> messageDTOS = messageMapper.findRecentContacts(userId);
//        for(var message : messageDTOS) {
//            System.out.println(message.getReceiverId());
//            System.out.println(message.getSenderId());
//        }
        return messageDTOS;
    }

    @Override
    @Transactional
    public void markAsRead(Long messageId) {
        messageMapper.markAsRead(messageId);
    }

    @Override
    @Transactional
    public void deleteMessage(Long messageId, Long userId) {
        // 检查权限 (只能删除自己发送的或接收的消息)
        PrivateMessage message = messageMapper.findById(messageId);
        if (message == null) {
            throw new BusinessException(404, "消息不存在");
        }
        if (!message.getSenderId().equals(userId) && !message.getReceiverId().equals(userId)) {
            throw new BusinessException(403, "无权限删除此消息");
        }
        messageMapper.delete(messageId);
    }
}
