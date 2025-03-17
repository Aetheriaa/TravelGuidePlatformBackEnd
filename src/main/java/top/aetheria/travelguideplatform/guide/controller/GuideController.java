package top.aetheria.travelguideplatform.guide.controller;

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
import top.aetheria.travelguideplatform.guide.dto.GuideCreateDTO;
import top.aetheria.travelguideplatform.guide.dto.GuideInfoDTO;
import top.aetheria.travelguideplatform.guide.dto.GuideListDTO;
import top.aetheria.travelguideplatform.guide.dto.GuideUpdateDTO;
import top.aetheria.travelguideplatform.guide.entity.Guide;
import top.aetheria.travelguideplatform.guide.entity.Tag;
import top.aetheria.travelguideplatform.guide.service.GuideService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/guides")
public class GuideController {

    private static final Logger logger = LoggerFactory.getLogger(GuideController.class); // 添加日志

    @Autowired
    private GuideService guideService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping
    public Result create(@Validated @RequestBody GuideCreateDTO guideCreateDTO, HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
        logger.info("Creating guide. userId: {}, DTO: {}", userId, guideCreateDTO); // INFO 级别日志
        guideService.create(userId, guideCreateDTO);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<GuideInfoDTO> getById(@PathVariable Long id, HttpServletRequest request) {
        // 从请求头中获取token,可能为空
        logger.info("Getting guide by ID: {}", id); // INFO 级别日志
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        Long userId = null;
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
            userId = jwtUtils.getUserIdFromToken(token);
        }
        GuideInfoDTO guideInfoDTO = guideService.getById(id, userId);
        logger.info("Guide found: {}", guideInfoDTO); // INFO 级别日志
        return Result.success(guideInfoDTO);
    }

    @PutMapping
    public Result update(@Validated @RequestBody GuideUpdateDTO guideUpdateDTO, HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
        logger.info("Updating guide. userId: {}, DTO: {}", userId, guideUpdateDTO);
        guideService.update(guideUpdateDTO, userId);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id, HttpServletRequest request) {
        logger.info("Deleting guide with ID: {}", id);
        // 从请求头中获取token
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long userId = jwtUtils.getUserIdFromToken(token);

        // 检查权限（例如，只能删除自己的攻略，或者管理员可以删除）
        // 这里只是简单示例，实际应用中需要根据业务规则进行更复杂的权限控制
        GuideInfoDTO guide = guideService.getById(id, userId);
        if (guide == null) {
            logger.warn("Guide with ID {} not found for deletion.", id); // WARN 级别日志
            return Result.error("该攻略不存在");
        }
        // 如果不是管理员，且不是自己发布的
        if (!userId.equals(guide.getUserId())) {
            logger.warn("User {} does not have permission to delete guide with ID: {}", userId, id); // WARN 级别日志
            return Result.error(403, "无权限");
        }

        guideService.delete(id);
        logger.info("Guide with ID {} deleted successfully.", id); // INFO 级别日志
        return Result.success();
    }

    @GetMapping
    public Result<PageResult<Guide>> list(GuideListDTO guideListDTO) {
        logger.debug("Listing guides with DTO: {}", guideListDTO); // DEBUG 级别日志
        PageResult<Guide> pageResult = guideService.list(guideListDTO);
        logger.info("Guide list returned with total {} guides.", pageResult.getTotal()); // INFO 级别日志
        return Result.success(pageResult);
    }

    @PostMapping("/{guideId}/like")
    public Result like(@PathVariable Long guideId, HttpServletRequest request){
        // 从请求头中获取token
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
        logger.info("Liking guide. userId: {}, guideId: {}", userId, guideId);
        guideService.like(guideId, userId);
        return Result.success();
    }

    @DeleteMapping("/{guideId}/like")
    public Result unlike(@PathVariable Long guideId, HttpServletRequest request){
        // 从请求头中获取token
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
        logger.info("Unliking guide. userId: {}, guideId: {}", userId, guideId);
        guideService.unlike(guideId, userId);
        return Result.success();
    }

    @PostMapping("/{guideId}/favorite")
    public Result favorite(@PathVariable Long guideId,HttpServletRequest request) {
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
        logger.info("favorite guide. userId: {}, guideId: {}", userId, guideId);
        guideService.favorite(guideId, userId);
        return Result.success();
    }

    @DeleteMapping("/{guideId}/favorite")
    public Result unfavorite(@PathVariable Long guideId,HttpServletRequest request){
        // 从请求头中获取token
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
        logger.info("unfavorite guide. userId: {}, guideId: {}", userId, guideId);
        guideService.unfavorite(guideId, userId);
        return Result.success();
    }
    // 获取个性化推荐攻略
    @GetMapping("/recommended")
    public Result<PageResult<GuideInfoDTO>> getRecommendedGuides(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "8") Integer pageSize)
    {
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        Long userId = null;
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
            userId = jwtUtils.getUserIdFromToken(token);
        }
        // 记录推荐请求和分页参数
        logger.info("Getting recommended guides for userId: {}, page: {}, pageSize: {}", userId, page, pageSize);
        PageResult<GuideInfoDTO> result = guideService.getRecommendedGuides(userId, page, pageSize);
        // 记录返回的攻略数量
        logger.info("Returned {} recommended guides.", result.getTotal());
        return Result.success(result);
    }


    // 获取热门攻略
    @GetMapping("/popular")
    public Result<PageResult<GuideInfoDTO>> getPopularGuides(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "8") Integer pageSize)
    {
        logger.info("Getting popular guides. page: {}, pageSize: {}", page, pageSize);
        PageResult<GuideInfoDTO>  result = guideService.getPopularGuides( page, pageSize);
        logger.info("Returned {} popular guides.", result.getTotal());
        return Result.success(result);
    }

    // 获取最新攻略
    @GetMapping("/latest")
    public Result<PageResult<GuideInfoDTO>> getLatestGuides(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "8") Integer pageSize)
    {
        logger.info("Getting latest guides. page: {}, pageSize: {}", page, pageSize);
        PageResult<GuideInfoDTO>  result = guideService.getLatestGuides( page, pageSize);
        logger.info("Returned {} latest guides.", result.getTotal());
        return Result.success(result);
    }

    // 获取热门标签
    @GetMapping("/popular-tags")
    public Result<List<Tag>> getPopularTags(@RequestParam(defaultValue = "10") int limit) {
        logger.info("Getting popular tags. limit: {}", limit);
        List<Tag> tags = guideService.getPopularTags(limit); // 调用 Service 层方法
        logger.info("Returned {} popular tags.", tags.size());
        return Result.success(tags);
    }

    // 新增：记录攻略浏览
    @PostMapping("/{guideId}/view")
    public Result recordGuideView(@PathVariable Long guideId, @RequestBody Map<String,Long> requestBody, HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
        logger.info("Recording guide view. userId: {}, guideId: {}", userId, guideId);
        if (userId == null) {
            return Result.error(401,"请先登录");
        }
        // 检查guideId是否为空
        if(guideId == null){
            return Result.error(400,"参数错误");
        }
        guideService.recordGuideView(userId, guideId);
        return Result.success();
    }
}