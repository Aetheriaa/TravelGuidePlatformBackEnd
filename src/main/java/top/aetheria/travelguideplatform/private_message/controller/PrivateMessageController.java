package top.aetheria.travelguideplatform.private_message.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.aetheria.travelguideplatform.common.constant.AppConstants;
import top.aetheria.travelguideplatform.common.utils.JwtUtils;
import top.aetheria.travelguideplatform.common.vo.Result;
import top.aetheria.travelguideplatform.order.dto.OrderCreateDTO;
import top.aetheria.travelguideplatform.order.dto.OrderInfoDTO;
import top.aetheria.travelguideplatform.order.dto.OrderListDTO;
import top.aetheria.travelguideplatform.order.entity.Order;
import top.aetheria.travelguideplatform.order.service.OrderService;
import top.aetheria.travelguideplatform.private_message.dto.PrivateMessageCreateDTO;
import top.aetheria.travelguideplatform.private_message.dto.PrivateMessageDTO;
import top.aetheria.travelguideplatform.private_message.dto.PrivateMessageListDTO;
import top.aetheria.travelguideplatform.private_message.service.PrivateMessageService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messages")
public class PrivateMessageController {

    @Autowired
    private PrivateMessageService messageService;
    @Autowired
    private JwtUtils jwtUtils;
    // 发送私信
    @PostMapping
    public Result<PrivateMessageDTO> sendMessage(@Validated @RequestBody PrivateMessageCreateDTO createDTO,
                                                 HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
        if(userId == null){
            return Result.error(401,"请先登录");
        }
        PrivateMessageDTO message = messageService.sendMessage(userId, createDTO);
        return Result.success(message);
    }

    // 获取与某个用户的私信对话
    @GetMapping("/{userId}")
    public Result<List<PrivateMessageDTO>> getConversation(@PathVariable Long userId, HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long currentUserId = jwtUtils.getUserIdFromToken(token);
        if(currentUserId == null){
            return Result.error(401,"请先登录");
        }
        List<PrivateMessageDTO> conversation = messageService.getConversation(currentUserId, userId);
        return Result.success(conversation);
    }

    // 获取私信列表 (最近联系人)
    @GetMapping("/recent")
    public Result<List<PrivateMessageListDTO>> getRecentContacts(HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
        if(userId == null){
            return Result.error(401,"请先登录");
        }
        List<PrivateMessageListDTO> recentContacts = messageService.getRecentContacts(userId);
        return Result.success(recentContacts);
    }

    // 删除私信
    @DeleteMapping("/{messageId}")
    public Result deleteMessage(@PathVariable Long messageId,HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
        if(userId == null){
            return Result.error(401,"请先登录");
        }
        messageService.deleteMessage(messageId, userId);
        return Result.success();
    }

    // 将私信标记为已读 (可选)
    @PutMapping("/{messageId}/read")
    public Result markAsRead(@PathVariable Long messageId) {
        messageService.markAsRead(messageId);
        return Result.success();
    }
}
