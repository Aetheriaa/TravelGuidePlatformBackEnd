package top.aetheria.travelguideplatform.order.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.aetheria.travelguideplatform.common.constant.AppConstants;
import top.aetheria.travelguideplatform.common.utils.JwtUtils;
import top.aetheria.travelguideplatform.common.vo.PageResult;
import top.aetheria.travelguideplatform.common.vo.Result;
import top.aetheria.travelguideplatform.order.dto.OrderCreateDTO;
import top.aetheria.travelguideplatform.order.dto.OrderInfoDTO;
import top.aetheria.travelguideplatform.order.dto.OrderListDTO;
import top.aetheria.travelguideplatform.order.entity.Order;
import top.aetheria.travelguideplatform.order.service.OrderService;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private JwtUtils jwtUtils;
    @PostMapping
    public Result<Order> createOrder(@Validated @RequestBody OrderCreateDTO orderCreateDTO, HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
        if(userId == null){
            return Result.error(401,"请先登录");
        }
        Order order = orderService.createOrder(userId, orderCreateDTO);
        return Result.success(order);
    }

    @GetMapping("/{id}")
    public Result<OrderInfoDTO> getOrderById(@PathVariable Long id,HttpServletRequest request) {
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
        if(userId == null){
            return Result.error(401,"请先登录");
        }
        OrderInfoDTO order = orderService.getOrderById(id);
        // 权限校验
        if(!order.getUserId().equals(userId)){
            return Result.error(403,"无权限");
        }
        return Result.success(order);
    }

    @GetMapping
    public Result<PageResult<OrderInfoDTO>> listOrders(OrderListDTO orderListDTO, HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
        if(userId == null){
            return Result.error(401,"请先登录");
        }
        PageResult<OrderInfoDTO> pageResult = orderService.listOrders(orderListDTO,userId);
        return Result.success(pageResult);
    }

    @PutMapping("/{id}/cancel")
    public Result cancelOrder(@PathVariable Long id, HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
        if(userId == null){
            return Result.error(401,"请先登录");
        }
        orderService.cancelOrder(id,userId);
        return Result.success();
    }

    @PutMapping("/{id}/pay")
    public Result payOrder(@PathVariable Long id, HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
//        System.out.println("qqqqq");
        if(userId == null){
            return Result.error(401,"请先登录");
        }
        // 验证权限, 只有订单所有者才能支付
        OrderInfoDTO order = orderService.getOrderById(id);
        if (!order.getUserId().equals(userId)) {
            return Result.error(403, "无权限支付此订单");
        }
        orderService.payOrder(id); // 这里假设支付成功
        return Result.success();
    }
}