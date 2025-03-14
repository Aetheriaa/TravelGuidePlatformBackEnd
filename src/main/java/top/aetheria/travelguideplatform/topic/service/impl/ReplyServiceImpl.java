package top.aetheria.travelguideplatform.topic.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.aetheria.travelguideplatform.common.exception.BusinessException;
import top.aetheria.travelguideplatform.topic.dto.ReplyCreateDTO;
import top.aetheria.travelguideplatform.topic.dto.ReplyInfoDTO;
import top.aetheria.travelguideplatform.topic.entity.Reply;
import top.aetheria.travelguideplatform.topic.entity.Topic;
import top.aetheria.travelguideplatform.topic.mapper.ReplyMapper;
import top.aetheria.travelguideplatform.topic.mapper.TopicMapper;
import top.aetheria.travelguideplatform.topic.service.ReplyService;
import top.aetheria.travelguideplatform.user.entity.User;
import top.aetheria.travelguideplatform.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReplyServiceImpl implements ReplyService {

    @Autowired
    private ReplyMapper replyMapper;

    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public Reply createReply(Long userId, ReplyCreateDTO createDTO) {
        // 1. 检查主题是否存在 (省略)

        // 2. 创建 Reply 对象
        Reply reply = new Reply();
        BeanUtils.copyProperties(createDTO, reply);
        reply.setUserId(userId);
        reply.setCreateTime(LocalDateTime.now());

        // 3. 插入数据库
        replyMapper.insert(reply);

        // 4. 更新主题的最后回复信息和回复数
        topicMapper.updateLastReply(createDTO.getTopicId(), userId, LocalDateTime.now());

        return reply;
    }

    @Override
    public List<ReplyInfoDTO> getRepliesByTopicId(Long topicId) {
        // 1. 查询回复列表
        List<Reply> replies = replyMapper.findByTopicId(topicId);

        // 2. 转换为 DTO 列表
        return replies.stream()
                .map(reply -> {
                    ReplyInfoDTO dto = new ReplyInfoDTO();
                    BeanUtils.copyProperties(reply, dto);

                    // 设置用户信息
                    User user = userMapper.findById(reply.getUserId());
                    if (user != null) {
                        dto.setUsername(user.getUsername());
                        dto.setUserAvatar(user.getAvatar());
                    }

                    // 设置父回复的用户名 (如果存在)
                    if (reply.getParentReplyId() != null) {
                        Reply parentReply = replyMapper.findById(reply.getParentReplyId());
                        if (parentReply != null) {
                            User parentUser = userMapper.findById(parentReply.getUserId());
                            if(parentUser != null){
                                dto.setParentReplyUsername(parentUser.getUsername());
                            }
                        }
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteReply(Long userId, Long replyId) {
        // 1. 查询回复信息
        Reply reply = replyMapper.findById(replyId);
        if (reply == null) {
            throw new BusinessException(404, "回复不存在");
        }

        // 2. 检查权限 (只有发布者才能删除)
        if (!reply.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权限删除此回复");
        }
        // 3. 删除回复
        replyMapper.delete(replyId);
        // TODO: 检查是否需要更新最后回复
    }
}
