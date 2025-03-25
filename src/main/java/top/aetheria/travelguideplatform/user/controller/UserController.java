package top.aetheria.travelguideplatform.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.code.kaptcha.Producer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.aetheria.travelguideplatform.common.constant.AppConstants;
import top.aetheria.travelguideplatform.common.exception.BusinessException;
import top.aetheria.travelguideplatform.common.utils.JwtUtils;
import top.aetheria.travelguideplatform.common.vo.PageResult;
import top.aetheria.travelguideplatform.common.vo.Result;
import top.aetheria.travelguideplatform.guide.dto.GuideInfoDTO;
import top.aetheria.travelguideplatform.user.dto.UserInfoDTO;
import top.aetheria.travelguideplatform.user.dto.UserLoginDTO;
import top.aetheria.travelguideplatform.user.dto.UserRegisterDTO;
import top.aetheria.travelguideplatform.user.dto.UserUpdateDTO;
import top.aetheria.travelguideplatform.user.entity.User;
import top.aetheria.travelguideplatform.user.service.UserService;
import top.aetheria.travelguideplatform.user.vo.LoginVO;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private Producer kaptchaProducer; // 注入 Producer
    @PostMapping("/register")
    public Result register(@Validated @RequestBody UserRegisterDTO userRegisterDTO) {
        // 验证邮箱验证码
        if (!userService.verifyEmailCode(userRegisterDTO.getEmail(), userRegisterDTO.getCode())) {
            return Result.error(400, "验证码错误或已过期");
        }
        logger.info("Registering new user: {}", userRegisterDTO);
        userService.register(userRegisterDTO);
        logger.info("User registration successful.");
        return Result.success();
    }
    // 发送邮箱验证码 (用于注册、找回密码等)
    @PostMapping("/send-verification-code")
    public Result sendVerificationCode(@RequestBody String email) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(email);
        return userService.sendEmailCode(jsonNode.get("email").asText());
    }

    // 忘记密码 - 重置密码
    @PostMapping("/reset-password")
    public Result resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        String newPassword = request.get("newPassword");

        if (!userService.verifyEmailCode(email, code)) {
            return Result.error(400, "验证码错误或已过期");
        }

        userService.resetPassword(email, newPassword);
        return Result.success("密码重置成功");
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@Validated @RequestBody UserLoginDTO userLoginDTO, HttpServletRequest request) {
        //1. 先从session中取出验证码
        HttpSession session = request.getSession();
        String captcha = (String) session.getAttribute("captcha");
        session.removeAttribute("captcha");//获取之后就删除
        //2. 验证码比较
        if(captcha == null || !captcha.equalsIgnoreCase(userLoginDTO.getCaptcha())){
//            throw new BusinessException(400,"验证码错误");
            return  Result.error("验证码错误");
        }
        logger.info("User login attempt: {}", userLoginDTO.getUsernameOrEmail());
        LoginVO loginVO = userService.login(userLoginDTO);
        logger.info("User login successful: {}", loginVO.getUsername());
        return Result.success(loginVO);
    }

    @GetMapping("/info")
    public Result<UserInfoDTO> info(HttpServletRequest request){
        // 从请求头中获取token
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        // 从token中解析出用户ID
        Long userId = jwtUtils.getUserIdFromToken(token);
        logger.info("Getting user info for userId: {}", userId);
        // 根据id查询
        User user = userService.getById(userId);
        // entity -> DTO
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        BeanUtils.copyProperties(user,userInfoDTO);
        logger.info("Returning user info for userId: {}", userId);
        return Result.success(userInfoDTO);

    }

    @PutMapping("/update")
    public Result update(HttpServletRequest request, @RequestBody UserUpdateDTO userUpdateDTO) {
        // 从请求头中获取token
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        // 从token中解析出用户ID
        Long userId = jwtUtils.getUserIdFromToken(token);
        logger.info("Updating user. userId: {}, DTO: {}", userId, userUpdateDTO);
        userService.update(userId, userUpdateDTO);
        return Result.success();
    }
    // 获取当前用户的浏览历史
    @GetMapping("/guide-history")
    public Result<PageResult<GuideInfoDTO>> getGuideHistory(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpServletRequest request
    ) {
        // 从请求头中获取token
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
        if(userId == null){
            return Result.error(401,"请先登录");
        }
        logger.info("Getting guide history for userId: {}, page: {}, pageSize: {}", userId, page, pageSize);
        PageResult<GuideInfoDTO> result = userService.getGuideHistory(userId, page, pageSize);
        return Result.success(result);
    }

    // 获取当前用户点赞的攻略
    @GetMapping("/liked-guides")
    public Result<PageResult<GuideInfoDTO>> getLikedGuides(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpServletRequest request
    ) {
        // 从请求头中获取token
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
        if(userId == null){
            return Result.error(401,"请先登录");
        }
        logger.info("Getting liked guides for userId: {}, page: {}, pageSize: {}", userId, page, pageSize);
        PageResult<GuideInfoDTO> result = userService.getLikedGuides(userId, page, pageSize);
        return Result.success(result);
    }

    // 获取当前用户收藏的攻略
    @GetMapping("/favorite-guides")
    public Result<PageResult<GuideInfoDTO>> getFavoriteGuides(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpServletRequest request
    ) {   // 从请求头中获取token
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
        if(userId == null){
            return Result.error(401,"请先登录");
        }
        logger.info("Getting favorite guides for userId: {}, page: {}, pageSize: {}", userId, page, pageSize);
        PageResult<GuideInfoDTO> result = userService.getFavoriteGuides(userId, page, pageSize);
        return Result.success(result);
    }
    // 关注用户
    @PostMapping("/{userId}/follow")
    public Result followUser(@PathVariable Long userId, HttpServletRequest request) {
        // 从请求头中获取token并解析出当前用户ID
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long currentUserId = jwtUtils.getUserIdFromToken(token);
        if(currentUserId == null){
            return Result.error(401,"请先登录");
        }
        // 记录关注操作的日志
        logger.info("User {} is attempting to follow user {}.", currentUserId, userId);

        userService.followUser(currentUserId, userId);
        return Result.success();
    }

    // 取消关注用户
    @DeleteMapping("/{userId}/follow")
    public Result unfollowUser(@PathVariable Long userId, HttpServletRequest request) {
        // 从请求头中获取token并解析出当前用户ID
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long currentUserId = jwtUtils.getUserIdFromToken(token);
        if(currentUserId == null){
            return Result.error(401,"请先登录");
        }
        // 记录取消关注操作的日志
        logger.info("User {} is attempting to unfollow user {}.", currentUserId, userId);

        userService.unfollowUser(currentUserId, userId);
        return Result.success();
    }

    // 获取用户的关注列表
    @GetMapping("/{userId}/following")
    public Result<List<User>> getFollowingList(@PathVariable Long userId) {
        logger.info("Getting following list for user ID: {}", userId);
        List<User> following = userService.getFollowingList(userId);
        logger.info("Returning following list with size: {} for user ID: {}", following.size(), userId);
        return Result.success(following);
    }

    // 获取用户的粉丝列表
    @GetMapping("/{userId}/followers")
    public Result<List<User>> getFollowerList(@PathVariable Long userId) {
        logger.info("Getting followers list for user ID: {}", userId);
        List<User> followers = userService.getFollowerList(userId);
        logger.info("Returning followers list with size: {} for user ID: {}", followers.size(), userId);
        return Result.success(followers);
    }
    // 检查当前用户是否关注了某个用户
    @GetMapping("/{userId}/is-following")
    public Result<Boolean> isFollowing(@PathVariable Long userId, HttpServletRequest request) {
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if(token == null || !token.startsWith(AppConstants.JWT_TOKEN_PREFIX)){
            return  Result.success(false); //如果没登录，直接返回false
        }
        token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        Long currentUserId = jwtUtils.getUserIdFromToken(token);
        if (currentUserId == null) {
            return Result.success(false); // 未登录，返回 false
        }
        logger.info("Checking if user {} is following user {}.", currentUserId, userId);
        boolean isFollowing = userService.isFollowing(currentUserId, userId);
        logger.info("User {} is following user {}: {}", currentUserId, userId, isFollowing);
        return Result.success(isFollowing);
    }

    @GetMapping("/{userId}")
    public Result<UserInfoDTO> getUserInfo(@PathVariable Long userId) {
        logger.info("Getting user info for user ID: {}", userId);
        User user = userService.getById(userId);
        if (user == null) {
            logger.warn("User with ID: {} not found", userId);
            return Result.error(404, "用户不存在");
        }
        // 将 User 对象转换为 UserInfoDTO 对象 (你可以创建一个 DTO 类)
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        BeanUtils.copyProperties(user,userInfoDTO);
        logger.info("Returning user info DTO for user ID: {}", userId);
        return Result.success(userInfoDTO);
    }

    // 获取关注数
    @GetMapping("/{userId}/following/count")
    public Result<Integer> getFollowingCount(@PathVariable Long userId) {
        logger.info("Getting following count for user ID: {}", userId);
        int count = userService.getFollowingCount(userId);
        logger.info("Returning following count: {} for user ID: {}", count, userId);
        return Result.success(count);
    }

    // 获取粉丝数
    @GetMapping("/{userId}/followers/count")
    public Result<Integer> getFollowerCount(@PathVariable Long userId) {
        logger.info("Getting follower count for user ID: {}", userId);
        int count = userService.getFollowerCount(userId);
        logger.info("Returning follower count: {} for user ID: {}", count, userId);
        return Result.success(count);
    }
    @GetMapping("/search")
    public Result<List<User>> searchUsers(@RequestParam String keyword) {
        logger.info("Searching users with keyword: {}", keyword);
        List<User> users = userService.searchUsers(keyword);
        logger.info("Returning {} users for search keyword: {}", users.size(), keyword);
        return Result.success(users);
    }

    // 获取验证码图片
    // 获取验证码图片
    @GetMapping("/captcha")
    public Result<Map<String, String>> getCaptcha(HttpServletRequest request) throws IOException {
        // 生成验证码文本
        String capText = kaptchaProducer.createText();
        // 将验证码文本存储到 session 中
        request.getSession().setAttribute("captcha", capText);
        // 创建验证码图片
        BufferedImage bi = kaptchaProducer.createImage(capText);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bi, "jpg", outputStream);

        // 将图片转换为 Base64 编码的字符串
        String base64Image = Base64.getEncoder().encodeToString(outputStream.toByteArray());

        // 构建返回数据
        Map<String, String> result = new HashMap<>();
        result.put("img", "data:image/jpeg;base64," + base64Image); // 添加 data:image/jpeg;base64, 前缀
//        result.put("key",key); //验证码对应的key,这里不需要key了
        return Result.success(result);
    }

}