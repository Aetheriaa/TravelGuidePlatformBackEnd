package top.aetheria.travelguideplatform.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import top.aetheria.travelguideplatform.common.constant.AppConstants;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/register")
    public Result register(@Validated @RequestBody UserRegisterDTO userRegisterDTO) {
        userService.register(userRegisterDTO);
        return Result.success();
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@Validated @RequestBody UserLoginDTO userLoginDTO) {
        LoginVO loginVO = userService.login(userLoginDTO);
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
        // 根据id查询
        User user = userService.getById(userId);

        // entity -> DTO
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        BeanUtils.copyProperties(user,userInfoDTO);
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

        PageResult<GuideInfoDTO> result = userService.getLikedGuides(userId, page, pageSize);
        return Result.success(result);
    }

    // 获取当前用户收藏的攻略
    @GetMapping("/favorite-guides")
    public Result<PageResult<GuideInfoDTO>> getFavoriteGuides(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpServletRequest request
    ) {
        Result<PageResult<GuideInfoDTO>> res;   // 从请求头中获取token
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
        if (userId == null) {
            res = Result.error(401, "请先登录");
        } else {
            PageResult<GuideInfoDTO> result = userService.getFavoriteGuides(userId, page, pageSize);
            res = Result.success(result);
        }
        return res;
    }

    // 关注用户
    @PostMapping("/{userId}/follow")
    public Result followUser(@PathVariable Long userId, HttpServletRequest request) {
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long currentUserId = jwtUtils.getUserIdFromToken(token);
        if(currentUserId == null){
            return Result.error(401,"请先登录");
        }
        userService.followUser(currentUserId, userId);
        return Result.success();
    }

    // 取消关注用户
    @DeleteMapping("/{userId}/follow")
    public Result unfollowUser(@PathVariable Long userId, HttpServletRequest request) {
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long currentUserId = jwtUtils.getUserIdFromToken(token);
        if(currentUserId == null){
            return Result.error(401,"请先登录");
        }
        userService.unfollowUser(currentUserId, userId);
        return Result.success();
    }

    // 获取用户的关注列表
    @GetMapping("/{userId}/following")
    public Result<List<User>> getFollowingList(@PathVariable Long userId) {
        List<User> following = userService.getFollowingList(userId);
        return Result.success(following);
    }

    // 获取用户的粉丝列表
    @GetMapping("/{userId}/followers")
    public Result<List<User>> getFollowerList(@PathVariable Long userId) {
        List<User> followers = userService.getFollowerList(userId);
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
        boolean isFollowing = userService.isFollowing(currentUserId, userId);
        return Result.success(isFollowing);
    }
    // 添加一个新的 API 接口，根据用户 ID 获取用户信息
    @GetMapping("/{userId}")
    public Result<UserInfoDTO> getUserInfo(@PathVariable Long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        // 将 User 对象转换为 UserInfoDTO 对象 (你可以创建一个 DTO 类)
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        BeanUtils.copyProperties(user,userInfoDTO);
        return Result.success(userInfoDTO);
    }

    // 获取关注数
    @GetMapping("/{userId}/following/count")
    public Result<Integer> getFollowingCount(@PathVariable Long userId) {
        int count = userService.getFollowingCount(userId);
        return Result.success(count);
    }

    // 获取粉丝数
    @GetMapping("/{userId}/followers/count")
    public Result<Integer> getFollowerCount(@PathVariable Long userId) {
        int count = userService.getFollowerCount(userId);
        return Result.success(count);
    }

    @GetMapping("/search")
    public Result<List<User>> searchUsers(@RequestParam String keyword) {
        List<User> users = userService.searchUsers(keyword);
        return Result.success(users);
    }
}