package top.aetheria.travelguideplatform.topic.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.aetheria.travelguideplatform.common.constant.AppConstants;
import top.aetheria.travelguideplatform.common.utils.JwtUtils;
import top.aetheria.travelguideplatform.common.vo.PageResult;
import top.aetheria.travelguideplatform.common.vo.Result;
import top.aetheria.travelguideplatform.topic.dto.TopicCreateDTO;
import top.aetheria.travelguideplatform.topic.dto.TopicInfoDTO;
import top.aetheria.travelguideplatform.topic.dto.TopicListDTO;
import top.aetheria.travelguideplatform.topic.dto.TopicUpdateDTO;
import top.aetheria.travelguideplatform.topic.entity.Topic;
import top.aetheria.travelguideplatform.topic.service.TopicService;

@RestController
@RequestMapping("/api/v1/topics") // 注意这里的 URL 前缀
public class TopicController {
    // 添加日志
    private static final Logger logger = LoggerFactory.getLogger(TopicController.class);

    @Autowired
    private TopicService topicService;

    @Autowired
    private JwtUtils jwtUtils;
    // 创建主题
    @PostMapping
    public Result<Topic> createTopic(@Validated @RequestBody TopicCreateDTO createDTO, HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
        // 添加日志
        logger.info("Creating topic. userId: {}, DTO: {}", userId, createDTO);
        if(userId == null){
            return Result.error(401,"请先登录");
        }
        Topic topic = topicService.createTopic(userId, createDTO);
        logger.info("Topic created with ID: {}", topic.getId()); // 假设你的 Topic 类有一个 getId() 方法
        return Result.success(topic);
    }

    // 获取主题详情
    @GetMapping("/{id}")
    public Result<TopicInfoDTO> getTopicById(@PathVariable Long id) {
        logger.info("Getting topic by ID: {}", id);
        TopicInfoDTO topic = topicService.getTopicById(id);
        //增加浏览次数,暂时不做
        topicService.addViewCount(topic.getId());
        logger.info("Returning topic: {}", topic);
        return Result.success(topic);
    }

    // 更新主题
    @PutMapping
    public Result updateTopic(@Validated @RequestBody TopicUpdateDTO updateDTO,HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
        logger.info("Updating topic. userId: {}, DTO: {}", userId, updateDTO);
        if(userId == null){
            return Result.error(401,"请先登录");
        }
        topicService.updateTopic(userId, updateDTO);
        return Result.success();
    }

    // 删除主题
    @DeleteMapping("/{id}")
    public Result deleteTopic(@PathVariable Long id,HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
        logger.info("Deleting topic. userId: {}, topicId: {}", userId, id);
        if(userId == null){
            return Result.error(401,"请先登录");
        }
        topicService.deleteTopic(userId, id);
        return Result.success();
    }

    // 获取主题列表
    @GetMapping
    public Result<PageResult<TopicInfoDTO>> listTopics(TopicListDTO topicListDTO) {
        logger.info("Listing topics with DTO: {}", topicListDTO);
        PageResult<TopicInfoDTO> pageResult = topicService.listTopics(topicListDTO);
        logger.info("Returned {} topics.", pageResult.getTotal());
        return Result.success(pageResult);
    }
}