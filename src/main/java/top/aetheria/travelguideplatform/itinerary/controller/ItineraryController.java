package top.aetheria.travelguideplatform.itinerary.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.aetheria.travelguideplatform.common.constant.AppConstants;
import top.aetheria.travelguideplatform.common.utils.JwtUtils;
import top.aetheria.travelguideplatform.common.vo.PageResult;
import top.aetheria.travelguideplatform.common.vo.Result;
import top.aetheria.travelguideplatform.itinerary.dto.ItineraryCreateDTO;
import top.aetheria.travelguideplatform.itinerary.dto.ItineraryInfoDTO;
import top.aetheria.travelguideplatform.itinerary.dto.ItineraryListDTO;
import top.aetheria.travelguideplatform.itinerary.dto.ItineraryUpdateDTO;
import top.aetheria.travelguideplatform.itinerary.entity.Itinerary;
import top.aetheria.travelguideplatform.itinerary.service.ItineraryService;

@RestController
@RequestMapping("/api/v1/itineraries")
public class ItineraryController {

    private static final Logger logger = LoggerFactory.getLogger(ItineraryController.class);

    @Autowired
    private ItineraryService itineraryService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping
    public Result<Itinerary> create(@Validated @RequestBody ItineraryCreateDTO itineraryCreateDTO, HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
        logger.info("Creating itinerary. userId: {}, DTO: {}", userId, itineraryCreateDTO);
        Itinerary itinerary = itineraryService.create(userId, itineraryCreateDTO);
        logger.info("Itinerary created with ID: {}", itinerary.getId());
        return Result.success(itinerary);
    }

    @GetMapping("/{id}")
    public Result<ItineraryInfoDTO> getById(@PathVariable Long id) {
        logger.info("Getting itinerary by ID: {}", id);
        ItineraryInfoDTO itinerary = itineraryService.getById(id);
        logger.info("Returning itinerary: {}", itinerary);
        return Result.success(itinerary);
    }

    @PutMapping
    public Result update(@Validated @RequestBody ItineraryUpdateDTO itineraryUpdateDTO, HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
        logger.info("Updating itinerary. userId: {}, DTO: {}", userId, itineraryUpdateDTO);
        itineraryService.update(itineraryUpdateDTO,userId);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id,HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
        logger.info("Deleting itinerary. userId: {}, itineraryId: {}", userId, id);
        itineraryService.delete(id,userId);
        return Result.success();
    }

    @GetMapping
    public Result<PageResult<Itinerary>> list(ItineraryListDTO itineraryListDTO,HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long userId = jwtUtils.getUserIdFromToken(token);

        logger.info("Listing itineraries with DTO: {}", itineraryListDTO);
        PageResult<Itinerary> pageResult = itineraryService.list(itineraryListDTO);
        return Result.success(pageResult);
    }
}