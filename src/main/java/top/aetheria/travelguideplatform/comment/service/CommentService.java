package top.aetheria.travelguideplatform.comment.service;

import top.aetheria.travelguideplatform.comment.dto.CommentCreateDTO;
import top.aetheria.travelguideplatform.comment.dto.CommentInfoDTO;
import top.aetheria.travelguideplatform.comment.dto.CommentUpdateDTO;
import top.aetheria.travelguideplatform.comment.entity.Comment;

import java.util.List;

public interface CommentService {
    void create(Long userId, CommentCreateDTO commentCreateDTO); // 创建评论

    List<CommentInfoDTO> getCommentsByGuideId(Long guideId); // 获取某个攻略下的所有评论（包含回复）

    void update(Long userId, CommentUpdateDTO commentUpdateDTO);//更新评论

    void delete(Long userId, Long commentId); // 删除评论
}