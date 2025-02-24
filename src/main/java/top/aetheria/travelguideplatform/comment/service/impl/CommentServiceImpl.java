package top.aetheria.travelguideplatform.comment.service.impl;

import top.aetheria.travelguideplatform.comment.dto.CommentCreateDTO;
import top.aetheria.travelguideplatform.comment.dto.CommentInfoDTO;
import top.aetheria.travelguideplatform.comment.dto.CommentUpdateDTO;
import top.aetheria.travelguideplatform.comment.entity.Comment;
import top.aetheria.travelguideplatform.comment.mapper.CommentMapper;
import top.aetheria.travelguideplatform.comment.service.CommentService;
import top.aetheria.travelguideplatform.common.constant.AppConstants;
import top.aetheria.travelguideplatform.common.exception.BusinessException;
import top.aetheria.travelguideplatform.user.entity.User;
import top.aetheria.travelguideplatform.user.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public void create(Long userId, CommentCreateDTO commentCreateDTO) {
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentCreateDTO, comment);
        comment.setUserId(userId);
        comment.setCreateTime(LocalDateTime.now());
        comment.setStatus(AppConstants.GUIDE_STATUS_PUBLISHED);
        commentMapper.insert(comment);
    }

    @Override
    public List<CommentInfoDTO> getCommentsByGuideId(Long guideId) {
        // 1. 查询所有一级评论
        List<Comment> topLevelComments = commentMapper.findAllByGuideId(guideId);

        // 2. 转换为 DTO，并递归构建回复树
        return topLevelComments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 将 Comment 转换为 CommentInfoDTO，并递归构建回复树
    private CommentInfoDTO convertToDto(Comment comment) {
        CommentInfoDTO dto = new CommentInfoDTO();
        BeanUtils.copyProperties(comment, dto);

        // 设置用户信息
        User user = userMapper.findById(comment.getUserId());
        if (user != null) {
            dto.setUsername(user.getUsername());
            dto.setUserAvatar(user.getAvatar());
        }
        // 如果是回复，设置父评论用户名
        if(comment.getParentCommentId() != null){
            Comment parentComment = commentMapper.findById(comment.getParentCommentId());
            if(parentComment != null){
                User parentUser = userMapper.findById(parentComment.getUserId());
                if(parentUser != null){
                    dto.setParentCommentUserName(parentUser.getUsername());
                }
            }
        }

        // 递归查询并设置子评论（回复）
        List<Comment> replies = commentMapper.findRepliesByParentId(comment.getId());
        if (replies != null && !replies.isEmpty()) {
            List<CommentInfoDTO> replyDTOs = replies.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            dto.setReplies(replyDTOs);
        } else {
            dto.setReplies(new ArrayList<>()); // 没有回复时，设置一个空列表，避免前端处理 null
        }
        return dto;
    }

    @Override
    @Transactional
    public void update(Long userId, CommentUpdateDTO commentUpdateDTO) {
        Comment comment = commentMapper.findById(commentUpdateDTO.getId());
        //判断是否存在
        if(comment == null){
            throw new BusinessException(404,"评论不存在");
        }
        // 检查权限（例如，只能修改自己的评论）
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权限修改此评论");
        }
        //更新
        comment.setContent(commentUpdateDTO.getContent());
        commentMapper.update(comment);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long commentId) {
        Comment comment = commentMapper.findById(commentId);
        if (comment == null) {
            throw new BusinessException(404, "评论不存在或已被删除");
        }

        // 检查权限（例如，只能删除自己的评论，或者管理员可以删除）
        // 这里只是简单示例，实际应用中需要根据业务规则进行更复杂的权限控制
        if (!userId.equals(comment.getUserId())) {
            throw new BusinessException(403, "无权限删除此评论");
        }
        //逻辑删除
        commentMapper.delete(commentId);
    }
}