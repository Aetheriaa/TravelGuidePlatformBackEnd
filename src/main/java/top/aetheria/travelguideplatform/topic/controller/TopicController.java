package top.aetheria.travelguideplatform.topic.controller;

import com.github.pagehelper.Page;
import jakarta.servlet.http.HttpServletRequest;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/topics") // 注意这里的 URL 前缀
public class TopicController {

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
        if(userId == null){
            return Result.error(401,"请先登录");
        }
        Topic topic = topicService.createTopic(userId, createDTO);
        return Result.success(topic);
    }

    // 获取主题详情
    @GetMapping("/{id}")
    public Result<TopicInfoDTO> getTopicById(@PathVariable Long id) {
        TopicInfoDTO topic = topicService.getTopicById(id);
        //增加浏览次数,暂时不做
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
        if(userId == null){
            return Result.error(401,"请先登录");
        }
        topicService.deleteTopic(userId, id);
        return Result.success();
    }

    // 获取主题列表
    @GetMapping
    public Result<PageResult<Topic>> listTopics(TopicListDTO topicListDTO) {
        PageResult<Topic> pageResult = topicService.listTopics(topicListDTO);
        return Result.success(pageResult);
    }
}
