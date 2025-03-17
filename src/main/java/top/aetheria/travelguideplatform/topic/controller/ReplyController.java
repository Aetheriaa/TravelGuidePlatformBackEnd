package top.aetheria.travelguideplatform.topic.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.aetheria.travelguideplatform.common.constant.AppConstants;
import top.aetheria.travelguideplatform.common.utils.JwtUtils;
import top.aetheria.travelguideplatform.common.vo.Result;
import top.aetheria.travelguideplatform.topic.dto.ReplyCreateDTO;
import top.aetheria.travelguideplatform.topic.dto.ReplyInfoDTO;
import top.aetheria.travelguideplatform.topic.entity.Reply;
import top.aetheria.travelguideplatform.topic.service.ReplyService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/replies") // 注意这里的 URL 前缀
public class ReplyController {

    private static final Logger logger = LoggerFactory.getLogger(ReplyController.class);

    @Autowired
    private ReplyService replyService;
    @Autowired
    private JwtUtils jwtUtils;
    // 创建回复
    @PostMapping
    public Result<Reply> createReply(@Validated @RequestBody ReplyCreateDTO createDTO, HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
        logger.info("Creating reply. userId: {}, DTO: {}", userId, createDTO);
        if(userId == null){
            return Result.error(401,"请先登录");
        }
        Reply reply = replyService.createReply(userId, createDTO);
        logger.info("Reply created with ID: {}", reply.getId());
        return Result.success(reply);
    }

    // 获取某个主题下的所有回复
    @GetMapping("/{topicId}")
    public Result<List<ReplyInfoDTO>> getRepliesByTopicId(@PathVariable Long topicId) {
        logger.info("Getting replies for topicId: {}", topicId);
        List<ReplyInfoDTO> replies = replyService.getRepliesByTopicId(topicId);
        logger.info("Returning {} replies for topicId: {}", replies.size(), topicId);
        return Result.success(replies);
    }

    // 删除回复 (需要权限验证)
    @DeleteMapping("/{replyId}")
    public Result deleteReply(@PathVariable Long replyId,HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
        logger.info("Deleting reply. userId: {}, replyId: {}", userId, replyId);
        if(userId == null){
            return Result.error(401,"请先登录");
        }
        replyService.deleteReply(userId, replyId);
        return Result.success();
    }
}