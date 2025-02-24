package top.aetheria.travelguideplatform.guide.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import top.aetheria.travelguideplatform.common.constant.AppConstants;
import top.aetheria.travelguideplatform.common.exception.BusinessException;
import top.aetheria.travelguideplatform.common.vo.PageResult;
import top.aetheria.travelguideplatform.guide.dto.GuideCreateDTO;
import top.aetheria.travelguideplatform.guide.dto.GuideInfoDTO;
import top.aetheria.travelguideplatform.guide.dto.GuideListDTO;
import top.aetheria.travelguideplatform.guide.dto.GuideUpdateDTO;
import top.aetheria.travelguideplatform.guide.entity.Favorite;
import top.aetheria.travelguideplatform.guide.entity.Guide;
import top.aetheria.travelguideplatform.guide.entity.Like;
import top.aetheria.travelguideplatform.guide.entity.Tag;
import top.aetheria.travelguideplatform.guide.mapper.FavoriteMapper;
import top.aetheria.travelguideplatform.guide.mapper.GuideMapper;
import top.aetheria.travelguideplatform.guide.mapper.LikeMapper;
import top.aetheria.travelguideplatform.guide.mapper.TagMapper;
import top.aetheria.travelguideplatform.guide.service.GuideService;
import top.aetheria.travelguideplatform.user.entity.User;
import top.aetheria.travelguideplatform.user.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GuideServiceImpl implements GuideService {

    @Autowired
    private GuideMapper guideMapper;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LikeMapper likeMapper;

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private TagMapper tagMapper;

    @Override
    @Transactional
    public void create(Long userId, GuideCreateDTO guideCreateDTO) {
        Guide guide = new Guide();
        BeanUtils.copyProperties(guideCreateDTO, guide);
        guide.setUserId(userId);
        guide.setCreateTime(LocalDateTime.now());
        guide.setUpdateTime(LocalDateTime.now());
        guide.setStatus(AppConstants.GUIDE_STATUS_PUBLISHED); // 默认发布状态
        guide.setViewCount(0);
        guide.setLikeCount(0);
        guide.setCommentCount(0);
        // 处理标签
        if (guideCreateDTO.getTags() != null && !guideCreateDTO.getTags().isEmpty()) {
            List<Tag> tagList = guideCreateDTO.getTags().stream()
                    .map(tagName -> {
                        Tag tag = tagMapper.findByName(tagName); //先找有没有这个tag
                        if (tag == null) {
                            tag = new Tag();
                            tag.setName(tagName);
                            tagMapper.insertOrUpdate(tag); //插入或更新
                            // tag id 会自动设置
                        }else {
                            tagMapper.insertOrUpdate(tag); //更新
                        }
                        return tag;
                    })
                    .collect(Collectors.toList());

            guideMapper.insertGuideTags(guide.getId(), tagList);
        }
    }

    @Override
    public GuideInfoDTO getById(Long id, Long userId) {
        //增加阅读量
        guideMapper.incrementViewCount(id);

        Guide guide = guideMapper.findByIdAndStatus(id, AppConstants.GUIDE_STATUS_PUBLISHED);
        if (guide == null) {
            throw new BusinessException(404, "攻略不存在或已被删除");
        }
        GuideInfoDTO guideInfoDTO = new GuideInfoDTO();
        BeanUtils.copyProperties(guide, guideInfoDTO);
        //查询作者信息
        User user = userMapper.findById(guide.getUserId());
        if (user != null) {
            guideInfoDTO.setAuthorName(user.getUsername());
            guideInfoDTO.setAuthorAvatar(user.getAvatar());
        }

        // 查询当前用户是否点赞、收藏
        if (userId != null) {
            Like like = new Like();
            like.setGuideId(id);
            like.setUserId(userId);
            guideInfoDTO.setLiked(likeMapper.countByGuideIdAndUserId(like) > 0);

            Favorite favorite = new Favorite();
            favorite.setGuideId(id);
            favorite.setUserId(userId);
            guideInfoDTO.setFavorited(favoriteMapper.countByGuideIdAndUserId(favorite) > 0);
        }
        return guideInfoDTO;
    }

    @Override
    @Transactional
    public void update(GuideUpdateDTO guideUpdateDTO, Long userId) {
        Guide guide = guideMapper.findById(guideUpdateDTO.getId());
        if (guide == null) {
            throw new BusinessException(404, "攻略不存在");
        }

        // 检查权限（例如，只能修改自己的攻略）
        if (!guide.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权限修改此攻略");
        }

        BeanUtils.copyProperties(guideUpdateDTO, guide);
        guide.setUpdateTime(LocalDateTime.now());
        // 先删除旧的标签关联
        guideMapper.deleteGuideTags(guide.getId());

        // 处理新的标签
        if (guideUpdateDTO.getTags() != null && !guideUpdateDTO.getTags().isEmpty()) {
            List<Tag> tagList = guideUpdateDTO.getTags().stream()
                    .map(tagName -> {
                        Tag tag = tagMapper.findByName(tagName);
                        if (tag == null) {
                            tag = new Tag();
                            tag.setName(tagName);
                            tagMapper.insertOrUpdate(tag); // 插入新标签
                        }else{
                            tagMapper.insertOrUpdate(tag);
                        }
                        return tag;
                    })
                    .collect(Collectors.toList());

            // 插入新的标签关联
            guideMapper.insertGuideTags(guide.getId(), tagList);
        }
        guideMapper.update(guide);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Guide guide = guideMapper.findById(id);
        if (guide == null) {
            throw new BusinessException(404, "攻略不存在");
        }
        // 逻辑删除
        guideMapper.updateStatus(id, AppConstants.GUIDE_STATUS_DELETED);
    }

    @Override
    public PageResult<Guide> list(GuideListDTO guideListDTO) {
        // 设置分页参数
        PageHelper.startPage(guideListDTO.getPage(), guideListDTO.getPageSize());

        // 执行查询
        List<Guide> guides = guideMapper.list(guideListDTO);

        // 获取分页结果
        Page<Guide> page = (Page<Guide>) guides;

        // 封装 PageResult
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    @Override
    @Transactional
    public void like(Long guideId, Long userId) {
        //判断是否已经点赞
        Like like = new Like();
        like.setGuideId(guideId);
        like.setUserId(userId);
        if (likeMapper.countByGuideIdAndUserId(like) > 0) {
            throw new BusinessException(400, "您已经点赞过了");
        }
        // 没有点赞，插入点赞数据
        like.setCreateTime(LocalDateTime.now());
        likeMapper.insert(like);
        //更新guide表中的点赞数
        Guide guide = guideMapper.findById(guideId);
        if (guide != null) {
            guide.setLikeCount(likeMapper.countByGuideId(guideId)); //重新计算点赞数
            guideMapper.update(guide);
        }
    }

    @Override
    @Transactional
    public void unlike(Long guideId, Long userId) {
        Like like = new Like();
        like.setGuideId(guideId);
        like.setUserId(userId);
        likeMapper.delete(like);

        //更新guide表中的点赞数
        Guide guide = guideMapper.findById(guideId);
        if (guide != null) {
            guide.setLikeCount(likeMapper.countByGuideId(guideId));
            guideMapper.update(guide);
        }
    }

    @Override
    @Transactional
    public void favorite(Long guideId, Long userId) {
        Favorite favorite = new Favorite();
        favorite.setGuideId(guideId);
        favorite.setUserId(userId);

        if (favoriteMapper.countByGuideIdAndUserId(favorite) > 0) {
            throw new BusinessException(400, "您已经收藏过了");
        }
        favorite.setCreateTime(LocalDateTime.now());
        favoriteMapper.insert(favorite);
    }

    @Override
    @Transactional
    public void unfavorite(Long guideId, Long userId) {
        Favorite favorite = new Favorite();
        favorite.setGuideId(guideId);
        favorite.setUserId(userId);
        favoriteMapper.delete(favorite);
    }

    @Override
    public PageResult<GuideInfoDTO> getRecommendedGuides(Long userId, int page, int pageSize) {
        // TODO: 在这里实现个性化推荐算法
        // 1. 根据 userId 获取用户的兴趣标签、浏览历史等信息 (需要额外的表和 Mapper 方法)
        // 2. 根据用户的兴趣标签，从数据库中查询相关的攻略
        // 3. 可以使用一些推荐算法 (例如，基于内容的推荐、协同过滤等) 来计算推荐结果
        // 4. 这里只是一个示例，返回一个空的列表，你需要根据你的实际需求来实现
        // 暂时返回所有
//        GuideListDTO guideListDTO = new GuideListDTO();
//        PageHelper.startPage(page,pageSize);
//        List<Guide> guides = guideMapper.list(guideListDTO);
//        Page<Guide> pageInfo = (Page<Guide>) guides;
//        List<GuideInfoDTO> guideInfoDTOS =  pageInfo.stream().map(guide->{
//            GuideInfoDTO guideInfoDTO = new GuideInfoDTO();
//            BeanUtils.copyProperties(guide,guideInfoDTO);
//            return guideInfoDTO;
//        }).collect(Collectors.toList());
//        return new PageResult<>(pageInfo.getTotal(),guideInfoDTOS);
        // 1. 获取用户的兴趣标签
        List<String> userTags = userMapper.findUserTags(userId);

        // 2. 创建 GuideListDTO 对象，并设置查询条件
        GuideListDTO guideListDTO = new GuideListDTO();
        guideListDTO.setTags(userTags);

        // 3. 设置分页参数
        PageHelper.startPage(page, pageSize);

        // 4. 调用 guideMapper.list 方法进行查询 (需要修改 list 方法)
        List<Guide> guides = guideMapper.list(guideListDTO);

        // 5. 获取分页结果
        Page<Guide> pageInfo = (Page<Guide>) guides;

        // 6. 将 Guide 列表转换为 GuideInfoDTO 列表,并查询作者信息
        List<GuideInfoDTO> guideInfoDTOS = guides.stream()
                .map(guide -> {
                    GuideInfoDTO guideInfoDTO = new GuideInfoDTO();
                    BeanUtils.copyProperties(guide, guideInfoDTO);
                    User user = userMapper.findById(guide.getUserId());
                    if(user!=null){
                        guideInfoDTO.setAuthorName(user.getUsername());
                        guideInfoDTO.setAuthorAvatar(user.getAvatar());
                    }
                    // 查询tags
                    List<Tag> tags = guideMapper.findTagsByGuideId(guide.getId());
                    guideInfoDTO.setTags(tags.stream().map(Tag::getName).collect(Collectors.toList()));

                    return guideInfoDTO;
                })
                .collect(Collectors.toList());

        // 7. 返回 PageResult 对象
        return new PageResult<>(pageInfo.getTotal(), guideInfoDTOS);
    }

    @Override
    public  PageResult<GuideInfoDTO> getPopularGuides(int page, int pageSize) {
        // TODO: 在这里实现获取热门攻略的逻辑
        // 1. 从数据库中查询浏览量、点赞数、评论数等指标较高的攻略
        // 2. 可以根据需要设置查询条件（例如，只查询最近一周的热门攻略）
        // 3. 这里只是一个示例，返回一个空的列表，你需要根据你的实际需求来实现
        GuideListDTO guideListDTO = new GuideListDTO();
        guideListDTO.setSortBy("viewCount");
        guideListDTO.setSortOrder("desc");
        PageHelper.startPage(page,pageSize);
        List<Guide> guides = guideMapper.list(guideListDTO);
        Page<Guide> pageInfo = (Page<Guide>) guides;
        List<GuideInfoDTO> guideInfoDTOS =  pageInfo.stream().map(guide->{
            GuideInfoDTO guideInfoDTO = new GuideInfoDTO();
            BeanUtils.copyProperties(guide,guideInfoDTO);
            return guideInfoDTO;
        }).collect(Collectors.toList());
        return new PageResult<>(pageInfo.getTotal(),guideInfoDTOS);
    }

    @Override
    public  PageResult<GuideInfoDTO> getLatestGuides(int page, int pageSize) {
        // TODO: 在这里实现获取最新攻略的逻辑
        // 1. 从数据库中查询最新发布的攻略（按发布时间倒序排序）
        // 2. 可以根据需要设置查询条件（例如，只查询某个分类下的最新攻略）
        // 3. 这里只是一个示例，返回一个空的列表，你需要根据你的实际需求来实现
        GuideListDTO guideListDTO = new GuideListDTO();
        guideListDTO.setSortBy("createTime");
        guideListDTO.setSortOrder("desc");
        PageHelper.startPage(page,pageSize);
        List<Guide> guides = guideMapper.list(guideListDTO);
        Page<Guide> pageInfo = (Page<Guide>) guides;
        List<GuideInfoDTO> guideInfoDTOS =  pageInfo.stream().map(guide->{
            GuideInfoDTO guideInfoDTO = new GuideInfoDTO();
            BeanUtils.copyProperties(guide,guideInfoDTO);
            return guideInfoDTO;
        }).collect(Collectors.toList());
        return new PageResult<>(pageInfo.getTotal(),guideInfoDTOS);
    }

    @Override
    public List<Tag> getPopularTags(int limit) {
        return tagMapper.findPopularTags(limit);
    }
}
