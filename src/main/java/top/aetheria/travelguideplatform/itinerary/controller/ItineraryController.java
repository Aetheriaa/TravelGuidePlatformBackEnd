package top.aetheria.travelguideplatform.itinerary.controller;

import jakarta.servlet.http.HttpServletRequest;
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
        Itinerary itinerary = itineraryService.create(userId, itineraryCreateDTO);
        return Result.success(itinerary);
    }

    @GetMapping("/{id}")
    public Result<ItineraryInfoDTO> getById(@PathVariable Long id) {
        ItineraryInfoDTO itinerary = itineraryService.getById(id);
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
        itineraryService.delete(id,userId);
        return Result.success();
    }

    @GetMapping
    public Result<PageResult<Itinerary>> list(ItineraryListDTO itineraryListDTO) {
        PageResult<Itinerary> pageResult = itineraryService.list(itineraryListDTO);
        return Result.success(pageResult);
    }
}