package top.aetheria.travelguideplatform.itinerary.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.aetheria.travelguideplatform.common.exception.BusinessException;
import top.aetheria.travelguideplatform.common.vo.PageResult;
import top.aetheria.travelguideplatform.itinerary.dto.ItineraryCreateDTO;
import top.aetheria.travelguideplatform.itinerary.dto.ItineraryDetailCreateDTO;
import top.aetheria.travelguideplatform.itinerary.dto.ItineraryDetailDTO;
import top.aetheria.travelguideplatform.itinerary.dto.ItineraryInfoDTO;
import top.aetheria.travelguideplatform.itinerary.dto.ItineraryListDTO;
import top.aetheria.travelguideplatform.itinerary.dto.ItineraryUpdateDTO;
import top.aetheria.travelguideplatform.itinerary.entity.Itinerary;
import top.aetheria.travelguideplatform.itinerary.entity.ItineraryDetail;
import top.aetheria.travelguideplatform.itinerary.mapper.ItineraryDetailMapper;
import top.aetheria.travelguideplatform.itinerary.mapper.ItineraryMapper;
import top.aetheria.travelguideplatform.itinerary.service.ItineraryService;
import top.aetheria.travelguideplatform.user.entity.User;
import top.aetheria.travelguideplatform.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItineraryServiceImpl implements ItineraryService {

    private static final Logger logger = LoggerFactory.getLogger(ItineraryServiceImpl.class);
    @Autowired
    private ItineraryMapper itineraryMapper;

    @Autowired
    private ItineraryDetailMapper itineraryDetailMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public Itinerary create(Long userId, ItineraryCreateDTO itineraryCreateDTO) {
        // 1. 创建行程对象
        Itinerary itinerary = new Itinerary();
        BeanUtils.copyProperties(itineraryCreateDTO, itinerary);
        itinerary.setUserId(userId);
        itinerary.setCreateTime(LocalDateTime.now());
        itinerary.setUpdateTime(LocalDateTime.now());

        // 2. 插入行程数据
        itineraryMapper.insert(itinerary);
        logger.info("Inserted itinerary with ID: {}", itinerary.getId());
        // 3. 插入行程详情数据
        if (itineraryCreateDTO.getDetails() != null && !itineraryCreateDTO.getDetails().isEmpty()) {
            for (ItineraryDetailCreateDTO detailDTO : itineraryCreateDTO.getDetails()) {
                ItineraryDetail detail = new ItineraryDetail();
                BeanUtils.copyProperties(detailDTO, detail);
                detail.setItineraryId(itinerary.getId()); // 设置行程ID
                itineraryDetailMapper.insert(detail);
            }
            logger.info("Inserted {} itinerary details for itinerary ID: {}", itineraryCreateDTO.getDetails().size(), itinerary.getId());
        }

        return itinerary;
    }

    @Override
    public ItineraryInfoDTO getById(Long id) {
        // 1. 查询行程信息
        Itinerary itinerary = itineraryMapper.findById(id);
        if (itinerary == null) {
            logger.warn("Itinerary with ID: {} not found", id);
            throw new BusinessException(404, "行程不存在");
        }

        // 2. 查询行程详情
        List<ItineraryDetail> details = itineraryDetailMapper.findByItineraryId(id);
        logger.debug("Found {} details for itinerary ID: {}", details.size(), id);

        // 3. 组装 ItineraryInfoDTO
        ItineraryInfoDTO itineraryInfoDTO = new ItineraryInfoDTO();
        BeanUtils.copyProperties(itinerary, itineraryInfoDTO);

        // 设置用户信息
        User user = userMapper.findById(itinerary.getUserId());
        if(user != null){
            itineraryInfoDTO.setUsername(user.getUsername());
        }

        // 设置行程详情
        List<ItineraryDetailDTO> detailDTOs = details.stream()
                .map(detail -> {
                    ItineraryDetailDTO detailDTO = new ItineraryDetailDTO();
                    BeanUtils.copyProperties(detail, detailDTO);
                    return detailDTO;
                })
                .collect(Collectors.toList());
        itineraryInfoDTO.setDetails(detailDTOs);

        logger.info("Returning itinerary info DTO for ID: {}", id);
        return itineraryInfoDTO;
    }

    @Override
    @Transactional
    public void update(ItineraryUpdateDTO itineraryUpdateDTO, Long userId) {
        // 1. 查询行程信息
        Itinerary itinerary = itineraryMapper.findById(itineraryUpdateDTO.getId());
        if (itinerary == null) {
            logger.warn("Attempt to update non-existent itinerary with ID: {}", itineraryUpdateDTO.getId());
            throw new BusinessException(404, "行程不存在");
        }
        // 2. 检查权限
        if (!itinerary.getUserId().equals(userId)) {
            logger.warn("User {} attempted to update itinerary {} without permission.", userId, itineraryUpdateDTO.getId());
            throw new BusinessException(403, "无权限修改此行程");
        }
        // 3. 更新行程信息
        BeanUtils.copyProperties(itineraryUpdateDTO, itinerary);
        itinerary.setUpdateTime(LocalDateTime.now());
        itineraryMapper.update(itinerary);
        logger.info("Updated itinerary with ID: {}", itinerary.getId());
        // 4. 删除旧的行程详情
        itineraryDetailMapper.deleteByItineraryId(itineraryUpdateDTO.getId());
        logger.info("Deleted old itinerary details for itinerary ID: {}", itineraryUpdateDTO.getId());
        // 5. 插入新的行程详情
        if (itineraryUpdateDTO.getDetails() != null && !itineraryUpdateDTO.getDetails().isEmpty()) {
            for (ItineraryDetailCreateDTO detailDTO : itineraryUpdateDTO.getDetails()) {
                ItineraryDetail detail = new ItineraryDetail();
                BeanUtils.copyProperties(detailDTO, detail);
                detail.setItineraryId(itineraryUpdateDTO.getId());
                itineraryDetailMapper.insert(detail);
            }
            logger.info("Inserted new itinerary details for itinerary ID: {}", itineraryUpdateDTO.getId());
        }
    }

    @Override
    @Transactional
    public void delete(Long id, Long userId) {
        // 1. 查询行程信息
        Itinerary itinerary = itineraryMapper.findById(id);
        if (itinerary == null) {
            logger.warn("Attempt to delete non-existent itinerary with ID: {}", id);
            throw new BusinessException(404, "行程不存在");
        }
        // 2. 检查权限
        if (!itinerary.getUserId().equals(userId)) {
            logger.warn("User {} attempted to delete itinerary {} without permission.", userId, id);
            throw new BusinessException(403, "无权限删除此行程");
        }
        // 2. 删除行程详情
        itineraryDetailMapper.deleteByItineraryId(id);
        logger.info("Deleted itinerary details for itinerary ID: {}", id);

        // 3. 删除行程
        itineraryMapper.delete(id);
        logger.info("Deleted itinerary with ID: {}", id);
    }

    @Override
    public PageResult<Itinerary> list(ItineraryListDTO itineraryListDTO) {
        // 设置分页参数
        PageHelper.startPage(itineraryListDTO.getPage(), itineraryListDTO.getPageSize());
        // 执行查询
        List<Itinerary> itineraries = itineraryMapper.list(itineraryListDTO);
        logger.debug("Retrieved {} itineraries from the database.", itineraries.size());
        // 获取分页结果
        Page<Itinerary> page = (Page<Itinerary>) itineraries;

        // 封装 PageResult
        return new PageResult<>(page.getTotal(), page.getResult());
    }
}