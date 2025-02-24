package top.aetheria.travelguideplatform.comment.controller;

import jakarta.servlet.http.HttpServletRequest;
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
        commentService.create(userId, commentCreateDTO);
        return Result.success();
    }

    @GetMapping("/{guideId}")
    public Result<List<CommentInfoDTO>> getCommentsByGuideId(@PathVariable Long guideId) {
        List<CommentInfoDTO> comments = commentService.getCommentsByGuideId(guideId);
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
            return Result.error(400,"Bad Request: Comment ID mismatch");
        }
        Long userId = jwtUtils.getUserIdFromToken(token);

        commentService.update(userId,commentUpdateDTO);
        return Result.success();
    }

    @DeleteMapping("/{commentId}")
    public Result delete(@PathVariable Long commentId,HttpServletRequest request){
        // 从请求头中获取token
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
        commentService.delete(userId, commentId);
        return Result.success();
    }
}