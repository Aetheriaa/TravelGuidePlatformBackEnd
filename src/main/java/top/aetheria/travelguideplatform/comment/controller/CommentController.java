package top.aetheria.travelguideplatform.comment.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.aetheria.travelguideplatform.comment.dto.CommentCreateDTO;
import top.aetheria.travelguideplatform.comment.dto.CommentInfoDTO;
import top.aetheria.travelguideplatform.comment.dto.CommentUpdateDTO;
import top.aetheria.travelguideplatform.comment.service.CommentService;
import top.aetheria.travelguideplatform.common.constant.AppConstants;
import top.aetheria.travelguideplatform.common.utils.JwtUtils;
import top.aetheria.travelguideplatform.common.vo.Result;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private CommentService commentService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping
    public Result create(@Validated @RequestBody CommentCreateDTO commentCreateDTO, HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
        logger.info("Creating comment. userId: {}, DTO: {}", userId, commentCreateDTO); // INFO 级别

        try {
            commentService.create(userId, commentCreateDTO);
            return Result.success();
        } catch (Exception e) {
            logger.error("Error creating comment.", e); // ERROR 级别
            return Result.error(500, "创建评论失败");
        }
    }

    @GetMapping("/{guideId}")
    public Result<List<CommentInfoDTO>> getCommentsByGuideId(@PathVariable Long guideId) {
        logger.debug("Getting comments for guideId: {}", guideId); // DEBUG 级别
        List<CommentInfoDTO> comments = commentService.getCommentsByGuideId(guideId);
        logger.debug("Found {} comments for guideId: {}", comments.size(), guideId);
        return Result.success(comments);
    }

    @PutMapping("/{commentId}")
    public Result update(@PathVariable Long commentId, @Validated @RequestBody CommentUpdateDTO commentUpdateDTO,HttpServletRequest request){
        // 从请求头中获取token
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        // 验证 DTO 的 ID 是否与路径参数匹配
        if (!commentId.equals(commentUpdateDTO.getId())) {
            logger.warn("Comment ID mismatch. Path ID: {}, DTO ID: {}", commentId, commentUpdateDTO.getId()); // WARN 级别
            return Result.error(400,"Bad Request: Comment ID mismatch");
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
        logger.info("Updating comment. userId: {}, commentId: {}, DTO: {}", userId, commentId, commentUpdateDTO);

        try {
            commentService.update(userId,commentUpdateDTO);
            return Result.success();
        } catch (Exception e) {
            logger.error("Error updating comment. userId: {}, commentId: {}", userId, commentId, e);
            return Result.error(500, "更新评论失败");
        }

    }

    @DeleteMapping("/{commentId}")
    public Result delete(@PathVariable Long commentId,HttpServletRequest request){
        // 从请求头中获取token
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
        logger.info("Deleting comment. userId: {}, commentId: {}", userId, commentId);
        try{
            commentService.delete(userId, commentId);
            return Result.success();
        }catch (Exception e){
            logger.error("Error deleting comment. userId: {}, commentId: {}", userId, commentId, e);
            return Result.error(500, "删除评论失败");
        }
    }
}