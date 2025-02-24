package top.aetheria.travelguideplatform.guide.service;

import top.aetheria.travelguideplatform.common.vo.PageResult;
import top.aetheria.travelguideplatform.guide.dto.GuideCreateDTO;
import top.aetheria.travelguideplatform.guide.dto.GuideInfoDTO;
import top.aetheria.travelguideplatform.guide.dto.GuideListDTO;
import top.aetheria.travelguideplatform.guide.dto.GuideUpdateDTO;
import top.aetheria.travelguideplatform.guide.entity.Guide;
import top.aetheria.travelguideplatform.guide.entity.Tag;

import java.util.List;

public interface GuideService {
    void create(Long userId, GuideCreateDTO guideCreateDTO);
    GuideInfoDTO getById(Long id,Long userId);
    void update(GuideUpdateDTO guideUpdateDTO,Long userId);
    void delete(Long id);
    PageResult<Guide> list(GuideListDTO guideListDTO);
    void like(Long guideId, Long userId);
    void unlike(Long guideId, Long userId);
    void favorite(Long guideId, Long userId);

    void unfavorite(Long guideId, Long userId);

    void recordGuideView(Long userId, Long guideId); // 记录用户浏览攻略
    PageResult<GuideInfoDTO> getRecommendedGuides(Long userId, int page, int pageSize);
    PageResult<GuideInfoDTO> getPopularGuides(int page, int pageSize);
    PageResult<GuideInfoDTO> getLatestGuides(int page, int pageSize);
    List<Tag> getPopularTags(int limit); // 获取热门标签

}
