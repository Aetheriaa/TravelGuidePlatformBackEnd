package top.aetheria.travelguideplatform.guide.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.aetheria.travelguideplatform.guide.entity.Tag;
import top.aetheria.travelguideplatform.guide.mapper.TagMapper;
import top.aetheria.travelguideplatform.guide.service.TagService;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagMapper tagMapper; // 注入 TagMapper
    @Override
    public List<Tag> getPopularTags(int limit) {
        return tagMapper.findPopularTags(limit);
    }
}