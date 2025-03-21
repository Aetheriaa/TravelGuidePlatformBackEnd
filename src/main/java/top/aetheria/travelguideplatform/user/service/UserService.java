package top.aetheria.travelguideplatform.user.service;

import top.aetheria.travelguideplatform.common.vo.PageResult;
import top.aetheria.travelguideplatform.common.vo.Result;
import top.aetheria.travelguideplatform.guide.dto.GuideInfoDTO;
import top.aetheria.travelguideplatform.user.dto.UserLoginDTO;
import top.aetheria.travelguideplatform.user.dto.UserRegisterDTO;
import top.aetheria.travelguideplatform.user.dto.UserUpdateDTO;
import top.aetheria.travelguideplatform.user.entity.User;
import top.aetheria.travelguideplatform.user.vo.LoginVO;

import java.util.List;

public interface UserService {
    void register(UserRegisterDTO userRegisterDTO);
    LoginVO login(UserLoginDTO userLoginDTO);
    User getById(Long id);
    void update(Long id, UserUpdateDTO userUpdateDTO);

    PageResult<GuideInfoDTO> getGuideHistory(Long userId, int page, int pageSize);

    PageResult<GuideInfoDTO> getLikedGuides(Long userId, int page, int pageSize);

    PageResult<GuideInfoDTO> getFavoriteGuides(Long userId, int page, int pageSize);

    void followUser(Long followerId, Long followingId);
    void unfollowUser(Long followerId, Long followingId);
    List<User> getFollowingList(Long userId); // 获取关注列表
    List<User> getFollowerList(Long userId);  // 获取粉丝列表
    boolean isFollowing(Long followerId, Long followingId); //检查是否关注

    int getFollowingCount(Long userId);
    int getFollowerCount(Long userId);

    List<User> searchUsers(String keyword); // 搜索用户

    //发送邮箱验证码
    Result sendEmailCode(String email);
    // 验证邮箱验证码 (用于注册、找回密码等)
    boolean verifyEmailCode(String email, String code);
    // 重置密码
    void resetPassword(String email, String newPassword);
}
