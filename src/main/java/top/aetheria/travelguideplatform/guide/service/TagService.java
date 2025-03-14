package top.aetheria.travelguideplatform.guide.service;

import top.aetheria.travelguideplatform.guide.entity.Tag;

import java.util.List;

public interface TagService {
    List<Tag> getPopularTags(int limit); // 获取热门标签
}