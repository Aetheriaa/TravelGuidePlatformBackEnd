package top.aetheria.travelguideplatform.guide.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/guides")
public class GuideController {

    private static final Logger logger = LoggerFactory.getLogger(GuideController.class); // 添加日志

    @Value("${app.upload.path}")
    private String uploadPath;
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

    @PostMapping("/upload-cover-image")
    @ResponseBody
    public Result<Map<String, String>> uploadCoverImage(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if (file.isEmpty()) {
            return Result.error(400, "上传文件不能为空");
        }

        try {
            // 1. 构建保存文件的目录 (使用配置的上传路径)
            File uploadDir = new File(uploadPath, "cover_images");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs(); // 如果目录不存在，创建目录
            }

            // 2. 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;

            // 3. 构建文件保存路径
            String filePath = uploadDir.getAbsolutePath() + File.separator + filename; // 使用绝对路径, 和 File.separator
            File dest = new File(filePath);

            // 4. 保存文件
            file.transferTo(dest);

            // 5. 构建并返回文件的完整 URL
            //    这里假设你的应用可以通过 /uploads 访问到上传的文件
            //    你需要根据你的实际部署情况来修改这里的 URL
            String serverName = request.getServerName(); // 获取服务器名 (例如 localhost)
            int serverPort = request.getServerPort();    // 获取服务器端口 (例如 8080)
            String contextPath = request.getContextPath(); // 获取上下文路径 (例如 /travel-platform)
            String fileUrl = "http://" + serverName + ":" + serverPort + contextPath + "/uploads/cover_images/" + filename;
//      String fileUrl =  "/uploads/cover_images/" + filename; //不能直接返回这个
            logger.info("Uploaded file URL: {}", fileUrl);
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            return Result.success(result);

        } catch (IOException e) {
            logger.error("文件上传失败", e);
            return Result.error(500, "文件上传失败");
        }

    }
    // 自动提取标签的接口
    @PostMapping("/extract-tags")
    public Result<List<String>> extractTags(@RequestBody Map<String, String> requestBody) {
        String content = requestBody.get("content");
        if (content == null || content.trim().isEmpty()) {
            return Result.error(400, "Content cannot be empty");
        }
        List<String> tags = guideService.extractTagsFromContent(content); // 调用 Service 层方法
        return Result.success(tags);
    }
}