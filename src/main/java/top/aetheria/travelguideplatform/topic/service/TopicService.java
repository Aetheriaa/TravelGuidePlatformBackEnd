package top.aetheria.travelguideplatform.topic.service;

import top.aetheria.travelguideplatform.common.vo.PageResult;
import top.aetheria.travelguideplatform.topic.dto.TopicCreateDTO;
import top.aetheria.travelguideplatform.topic.dto.TopicInfoDTO;
import top.aetheria.travelguideplatform.topic.dto.TopicListDTO;
import top.aetheria.travelguideplatform.topic.dto.TopicUpdateDTO;
import top.aetheria.travelguideplatform.topic.entity.Topic;

public interface TopicService {

    Topic createTopic(Long userId, TopicCreateDTO createDTO);

    TopicInfoDTO getTopicById(Long id);

    void updateTopic(Long userId, TopicUpdateDTO updateDTO);

    void deleteTopic(Long userId, Long topicId);

    PageResult<TopicInfoDTO> listTopics(TopicListDTO topicListDTO);

    public void addViewCount(Long id);
}