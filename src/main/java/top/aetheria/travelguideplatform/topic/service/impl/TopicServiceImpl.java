package top.aetheria.travelguideplatform.topic.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.aetheria.travelguideplatform.common.exception.BusinessException;
import top.aetheria.travelguideplatform.common.vo.PageResult;
import top.aetheria.travelguideplatform.topic.dto.TopicCreateDTO;
import top.aetheria.travelguideplatform.topic.dto.TopicInfoDTO;
import top.aetheria.travelguideplatform.topic.dto.TopicListDTO;
import top.aetheria.travelguideplatform.topic.dto.TopicUpdateDTO;
import top.aetheria.travelguideplatform.topic.entity.Topic;
import top.aetheria.travelguideplatform.user.entity.User;
import top.aetheria.travelguideplatform.topic.mapper.ReplyMapper;
import top.aetheria.travelguideplatform.topic.mapper.TopicMapper;
import top.aetheria.travelguideplatform.topic.service.TopicService;
import top.aetheria.travelguideplatform.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TopicServiceImpl implements TopicService {

    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ReplyMapper replyMapper; // 注入 ReplyMapper

    @Override
    @Transactional
    public Topic createTopic(Long userId, TopicCreateDTO createDTO) {
        // 1. 检查用户是否存在 (省略)

        // 2. 创建 Topic 对象
        Topic topic = new Topic();
        BeanUtils.copyProperties(createDTO, topic);
        topic.setUserId(userId);
        topic.setCreateTime(LocalDateTime.now());
        topic.setUpdateTime(LocalDateTime.now());
        topic.setViewCount(0);
        topic.setReplyCount(0);
        topic.setLastReplyUserId(null); // 刚创建的主题，没有最后回复
        topic.setLastReplyTime(null);

        // 3. 插入数据库
        topicMapper.insert(topic);

        return topic;
    }

    @Override
    public TopicInfoDTO getTopicById(Long id) {
        // 1. 查询主题信息
        Topic topic = topicMapper.findById(id);
        if (topic == null) {
            throw new BusinessException(404, "主题不存在");
        }

        // 2. 查询用户信息
        User user = userMapper.findById(topic.getUserId());

        // 3. 组装 TopicInfoDTO
        TopicInfoDTO topicInfoDTO = new TopicInfoDTO();
        BeanUtils.copyProperties(topic, topicInfoDTO);

        if (user != null) {
            topicInfoDTO.setUsername(user.getUsername());
            topicInfoDTO.setUserAvatar(user.getAvatar());
        }
        // 设置最后回复着信息
        if(topic.getLastReplyUserId() != null){
            User lastReplyUser = userMapper.findById(topic.getLastReplyUserId());
            if (lastReplyUser != null) {
                topicInfoDTO.setLastReplyUsername(lastReplyUser.getUsername());
            }
        }
        return topicInfoDTO;
    }

    @Override
    @Transactional
    public void updateTopic(Long userId, TopicUpdateDTO updateDTO) {
        // 1. 查询主题信息
        Topic topic = topicMapper.findById(updateDTO.getId());
        if (topic == null) {
            throw new BusinessException(404, "主题不存在");
        }

        // 2. 检查权限 (只有发布者才能修改)
        if (!topic.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权限修改此主题");
        }

        // 3. 更新主题信息
        BeanUtils.copyProperties(updateDTO, topic);
        topic.setUpdateTime(LocalDateTime.now());
        topicMapper.update(topic);
    }

    @Override
    @Transactional
    public void deleteTopic(Long userId, Long topicId) {
        // 1. 查询主题信息
        Topic topic = topicMapper.findById(topicId);
        if (topic == null) {
            throw new BusinessException(404, "主题不存在");
        }

        // 2. 检查权限 (只有发布者才能删除)
        if (!topic.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权限删除此主题");
        }
        // 3. 删除主题 (逻辑删除或物理删除，根据你的需求)
        //    这里使用物理删除
        //先删除回复
        replyMapper.deleteByTopicId(topicId);
        // 再删除主题
        topicMapper.delete(topicId);

    }

    @Override
    public PageResult<TopicInfoDTO> listTopics(TopicListDTO topicListDTO) {
        // 1. 设置分页参数
        PageHelper.startPage(topicListDTO.getPage(), topicListDTO.getPageSize());

        // 2. 执行查询
        List<Topic> topics = topicMapper.list(topicListDTO);

        // 3. 获取 PageHelper 的分页结果信息
        PageInfo<Topic> pageInfo = new PageInfo<>(topics);
        List<TopicInfoDTO> topicInfos = new ArrayList<>();
        for (var topic : topics) {
            topicInfos.add(getTopicById(topic.getId()));
        }

        // 4. 封装 PageResult
        return new PageResult<>(pageInfo.getTotal(), topicInfos);
    }


    public void addViewCount(Long id) {
        // 1. 增加浏览次数
        topicMapper.incrementViewCount(id);
    }

}