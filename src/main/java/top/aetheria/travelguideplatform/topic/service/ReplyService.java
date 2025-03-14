package top.aetheria.travelguideplatform.topic.service;

import top.aetheria.travelguideplatform.topic.dto.ReplyCreateDTO;
import top.aetheria.travelguideplatform.topic.dto.ReplyInfoDTO;
import top.aetheria.travelguideplatform.topic.entity.Reply;

import java.util.List;

public interface ReplyService {
    Reply createReply(Long userId, ReplyCreateDTO createDTO);

    List<ReplyInfoDTO> getRepliesByTopicId(Long topicId);

    void deleteReply(Long userId, Long replyId); // userId 用于权限检查
}
