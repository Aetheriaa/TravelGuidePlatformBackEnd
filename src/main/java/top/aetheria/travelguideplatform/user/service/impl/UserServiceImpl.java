package top.aetheria.travelguideplatform.user.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import top.aetheria.travelguideplatform.common.constant.AppConstants;
import top.aetheria.travelguideplatform.common.exception.BusinessException;
import top.aetheria.travelguideplatform.common.utils.JwtUtils;
import top.aetheria.travelguideplatform.common.vo.PageResult;
import top.aetheria.travelguideplatform.guide.dto.GuideInfoDTO;
import top.aetheria.travelguideplatform.guide.entity.Guide;
import top.aetheria.travelguideplatform.guide.entity.Tag;
import top.aetheria.travelguideplatform.guide.mapper.FavoriteMapper;
import top.aetheria.travelguideplatform.guide.mapper.GuideMapper;
import top.aetheria.travelguideplatform.guide.mapper.LikeMapper;
import top.aetheria.travelguideplatform.guide.mapper.TagMapper;
import top.aetheria.travelguideplatform.user.mapper.UserGuideHistoryMapper;
import top.aetheria.travelguideplatform.user.dto.UserLoginDTO;
import top.aetheria.travelguideplatform.user.dto.UserRegisterDTO;
import top.aetheria.travelguideplatform.user.dto.UserUpdateDTO;
import top.aetheria.travelguideplatform.user.entity.User;
import top.aetheria.travelguideplatform.user.mapper.UserMapper;
import top.aetheria.travelguideplatform.user.service.UserService;
import top.aetheria.travelguideplatform.user.vo.LoginVO;

import java.beans.FeatureDescriptor;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import  org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserGuideHistoryMapper userGuideHistoryMapper;

    @Autowired
    private GuideMapper guideMapper;

    @Autowired
    private LikeMapper likeMapper;

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public void register(UserRegisterDTO userRegisterDTO) {
        // 1. 检查用户名是否已存在
        if (userMapper.findByUsername(userRegisterDTO.getUsername()) != null) {
            throw new BusinessException(400,"用户名已存在");
        }

        // 2. 检查邮箱是否已存在
        if (userMapper.findByEmail(userRegisterDTO.getEmail()) != null) {
            throw new BusinessException(400,"邮箱已存在");
        }

        // 3. 将DTO转换为实体类
        User user = new User();
        BeanUtils.copyProperties(userRegisterDTO, user);

        // 4. 密码加密
        String hashedPassword = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        user.setPassword(hashedPassword);

        //5. 设置状态和注册时间
        user.setStatus(AppConstants.USER_STATUS_NORMAL);
        user.setRegistrationTime(LocalDateTime.now());
        user.setAvatar(String.format("https://api.dicebear.com/9.x/%s/svg?seed=%s","pixel-art", user.getUsername()));

        // 6. 插入数据库
        userMapper.insert(user);
        logger.info("Registered new user: {}", user.getUsername()); // 记录新用户注册
    }

    @Override
    public LoginVO login(UserLoginDTO userLoginDTO) {
        // 1. 根据用户名或邮箱查询用户
        User user = userMapper.findByUsernameOrEmail(userLoginDTO.getUsernameOrEmail());

        // 2. 检查用户是否存在
        if (user == null) {
            logger.warn("Login attempt with invalid username/email: {}", userLoginDTO.getUsernameOrEmail());
            throw new BusinessException(400,"用户名或密码错误");
        }

        // 3. 检查密码是否正确
        String hashedPassword = DigestUtils.md5DigestAsHex(userLoginDTO.getPassword().getBytes());
        if (!hashedPassword.equals(user.getPassword())) {
            logger.warn("Login attempt with incorrect password for user: {}", user.getUsername());
            throw new BusinessException(400,"用户名或密码错误");
        }
        //更新最后登录时间
        userMapper.updateLastLoginTime(user.getId(), LocalDateTime.now());

        // 4. 生成JWT
        String token = jwtUtils.generateToken(user.getId());

        //5. 封装VO
        LoginVO loginVO = new LoginVO();
        loginVO.setId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setToken(token);
        logger.info("User logged in: {}", user.getUsername());
        return loginVO;

    }
    @Override
    public User getById(Long id){
        User user = userMapper.findById(id);
        if (user == null){
            logger.warn("Attempt to get non-existent user with ID: {}", id);
            throw new BusinessException(404,"用户不存在");
        }
        logger.info("user:{}",user);
        return  user;
    }

    @Override
    public void update(Long id, UserUpdateDTO userUpdateDTO) {
        // 1. 查询用户
        User user = userMapper.findById(id);
        if(user == null) {
            logger.warn("Attempt to update non-existent user with ID: {}", id);
            throw new BusinessException(404, "用户不存在");
        }
        // 2. 更新字段，仅更新不为null的字段
        BeanUtils.copyProperties(userUpdateDTO, user, getNullPropertyNames(userUpdateDTO));

        // 3. 更新到数据库
        userMapper.update(user);
        logger.info("Updated user with ID: {}", id);
    }

    // 工具方法：获取对象中为null的属性名
    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);
    }
//    @Override
//    public List<String> getUserTags(Long userId) {
//        logger.info("Getting tags for user ID: {}", userId);
//        List<String> tags = userMapper.findUserTags(userId);
//        logger.info("Returning {} tags for user ID: {}", tags.size(), userId);
//        return  tags;
//    }

    @Override
    public PageResult<GuideInfoDTO> getGuideHistory(Long userId, int page, int pageSize) {

        // 查询浏览历史数据
        PageHelper.startPage(page,pageSize);
        List<Map<String, Object>> historyEntries = userGuideHistoryMapper.findRecentGuidesWithViewTime(userId, page, pageSize);
        Page<Map<String, Object>> pageInfo = (Page<Map<String, Object>>) historyEntries;
        // 将浏览历史数据转换为 GuideInfoDTO 列表
        List<GuideInfoDTO> guideInfoDTOList = historyEntries.stream()
                .map(entry -> {
                    Long guideId = ((Number) entry.get("guide_id")).longValue();
                    Guide guide = guideMapper.findById(guideId);
                    if (guide == null) {
                        return null; // 或者跳过这条记录
                    }
                    GuideInfoDTO dto = new GuideInfoDTO();
                    BeanUtils.copyProperties(guide, dto);
                    // 设置其他属性，例如作者信息、点赞/收藏状态等 (如果需要)
                    // ...
                    User user = userMapper.findById(guide.getUserId());
                    if (user != null) {
                        dto.setAuthorName(user.getUsername());
                        dto.setAuthorAvatar(user.getAvatar());
                    }

                    String viewTimeString = entry.get("view_time").toString();
                    // 定义 DateTimeFormatter，直接处理带一位小数的秒
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
                    // 解析字符串为 LocalDateTime 对象
                    LocalDateTime time = LocalDateTime.parse(viewTimeString, formatter);
                    dto.setCreateTime(time);

                    return dto;
                })
                .filter(Objects::nonNull) // 过滤掉 null 值
                .collect(Collectors.toList());
        logger.info("guideInfoDTOList.size:{}",guideInfoDTOList.size());
        // 返回分页结果
        return new PageResult<>(pageInfo.getTotal(), guideInfoDTOList);
    }

    @Override
    public PageResult<GuideInfoDTO> getLikedGuides(Long userId, int page, int pageSize) {
        // 使用 PageHelper 设置分页参数
        PageHelper.startPage(page, pageSize);
        // 查询用户点赞的攻略,并直接转换为GuideInfoDTO
        List<Guide> guides = likeMapper.findLikedGuideIdsByUserId(userId);
        // 获取分页信息
        PageInfo<Guide> pageInfo = new PageInfo<>(guides);
        List<GuideInfoDTO> guideInfoDTOS = guides.stream().map(guide -> {
            GuideInfoDTO guideInfoDTO = new GuideInfoDTO();
            BeanUtils.copyProperties(guide,guideInfoDTO);
            // 查询作者信息
            User user = userMapper.findById(guide.getUserId());
            if (user != null) {
                guideInfoDTO.setAuthorName(user.getUsername());
                guideInfoDTO.setAuthorAvatar(user.getAvatar());
            }
            // 查询tags
            List<Tag> tags = tagMapper.findByGuideId(guide.getId());
            guideInfoDTO.setTags(tags.stream().map(Tag::getName).collect(Collectors.toList()));
            return guideInfoDTO;
        }).collect(Collectors.toList());
        logger.info("Retrieved {} liked guides for user ID: {}", guideInfoDTOS.size(), userId);
        return new PageResult<>(pageInfo.getTotal(),guideInfoDTOS);
    }


    @Override
    public PageResult<GuideInfoDTO> getFavoriteGuides(Long userId, int page, int pageSize) {
        // 设置分页参数
        PageHelper.startPage(page, pageSize);
        // 查询用户收藏的攻略
        List<Guide> guides = favoriteMapper.findFavoriteGuideIdsByUserId(userId);
        // 获取分页信息
        PageInfo<Guide> pageInfo = new PageInfo<>(guides);
        List<GuideInfoDTO> guideInfoDTOS =  guides.stream().map(guide->{
            GuideInfoDTO guideInfoDTO = new GuideInfoDTO();
            BeanUtils.copyProperties(guide,guideInfoDTO);
            // 查询作者信息
            User user = userMapper.findById(guide.getUserId());
            if (user != null) {
                guideInfoDTO.setAuthorName(user.getUsername());
                guideInfoDTO.setAuthorAvatar(user.getAvatar());
            }
            // 查询tags
            List<Tag> tags = tagMapper.findByGuideId(guide.getId());
            guideInfoDTO.setTags(tags.stream().map(Tag::getName).collect(Collectors.toList()));
            return guideInfoDTO;
        }).collect(Collectors.toList());
        logger.info("Retrieved {} favorite guides for user ID: {}", guideInfoDTOS.size(), userId);
        return new PageResult<>(pageInfo.getTotal(),guideInfoDTOS);
    }

    @Override
    @Transactional
    public void followUser(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new BusinessException(400, "不能关注自己");
        }
        // 检查用户是否存在 (省略)

        // 检查是否已经关注
        if (userMapper.isFollowing(followerId, followingId)) {
            throw new BusinessException(400, "已经关注过了");
        }

        userMapper.insertFollow(followerId, followingId);
        logger.info("User {} followed user {}.", followerId, followingId);
    }

    @Override
    @Transactional
    public void unfollowUser(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new BusinessException(400, "不能关注自己");
        }
        // 检查用户是否存在 (省略)

        userMapper.deleteFollow(followerId, followingId);
        logger.info("User {} unfollowed user {}.", followerId, followingId);
    }

    @Override
    public List<User> getFollowingList(Long userId) {
        logger.info("Getting following list for user ID: {}", userId);
        return userMapper.findFollowing(userId);
    }

    @Override
    public List<User> getFollowerList(Long userId) {
        logger.info("Getting followers list for user ID: {}", userId);
        return userMapper.findFollowers(userId);
    }
    @Override
    public boolean isFollowing(Long followerId, Long followingId) {
        return userMapper.isFollowing(followerId, followingId);
    }

    @Override
    public int getFollowingCount(Long userId) {
        logger.info("Getting following count for user ID: {}", userId);
        return userMapper.getFollowingCount(userId);
    }

    @Override
    public int getFollowerCount(Long userId) {
        logger.info("Getting followers count for user ID: {}", userId);
        return userMapper.getFollowerCount(userId);
    }

    @Override
    public List<User> searchUsers(String keyword) {
        return userMapper.searchUsers(keyword);
    }
}