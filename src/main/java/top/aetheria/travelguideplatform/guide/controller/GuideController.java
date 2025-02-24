package top.aetheria.travelguideplatform.guide.controller;

import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/guides")
public class GuideController {

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
        guideService.create(userId, guideCreateDTO);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<GuideInfoDTO> getById(@PathVariable Long id, HttpServletRequest request) {
        // 从请求头中获取token,可能为空
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        Long userId = null;
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
            userId = jwtUtils.getUserIdFromToken(token);
        }
        GuideInfoDTO guideInfoDTO = guideService.getById(id, userId);
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
        guideService.update(guideUpdateDTO, userId);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id, HttpServletRequest request) {
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
            return Result.error("该攻略不存在");
        }
        // 如果不是管理员，且不是自己发布的
        if (!userId.equals(guide.getUserId())) {
            return Result.error(403, "无权限");
        }

        guideService.delete(id);
        return Result.success();
    }

    @GetMapping
    public Result<PageResult<Guide>> list(GuideListDTO guideListDTO) {
        PageResult<Guide> pageResult = guideService.list(guideListDTO);
        return Result.success(pageResult);
    }

    @PostMapping("/{guideId}/like")
    public Result like(@PathVariable Long guideId, HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
        guideService.like(guideId, userId);
        return Result.success();
    }

    @DeleteMapping("/{guideId}/like")
    public Result unlike(@PathVariable Long guideId, HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
        guideService.unlike(guideId, userId);
        return Result.success();
    }

    @PostMapping("/{guideId}/favorite")
    public Result favorite(@PathVariable Long guideId, HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
        guideService.favorite(guideId, userId);
        return Result.success();
    }

    @DeleteMapping("/{guideId}/favorite")
    public Result unfavorite(@PathVariable Long guideId, HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
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
        // 从请求头中获取token,可能为空
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        Long userId = null;
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
            userId = jwtUtils.getUserIdFromToken(token);
        }
        PageResult<GuideInfoDTO> result = guideService.getRecommendedGuides(userId, page, pageSize);
        return Result.success(result);
    }

    // 获取热门攻略
    @GetMapping("/popular")
    public Result<PageResult<GuideInfoDTO>> getPopularGuides(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "8") Integer pageSize)
    {
        PageResult<GuideInfoDTO> result = guideService.getPopularGuides( page, pageSize);
        return Result.success(result);
    }

    // 获取最新攻略
    @GetMapping("/latest")
    public Result<PageResult<GuideInfoDTO>> getLatestGuides(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "8") Integer pageSize)
    {
        PageResult<GuideInfoDTO> result = guideService.getLatestGuides( page, pageSize);
        return Result.success(result);
    }

    @GetMapping("/popular-tags")
    public Result<List<Tag>> getPopularTags(@RequestParam(defaultValue = "10") int limit) {
        List<Tag> tags = guideService.getPopularTags(limit); // 调用 Service 层方法
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