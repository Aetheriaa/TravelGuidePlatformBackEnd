package top.aetheria.travelguideplatform.order.controller;

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
import top.aetheria.travelguideplatform.order.dto.OrderCreateDTO;
import top.aetheria.travelguideplatform.order.dto.OrderInfoDTO;
import top.aetheria.travelguideplatform.order.dto.OrderListDTO;
import top.aetheria.travelguideplatform.order.entity.Order;
import top.aetheria.travelguideplatform.order.service.OrderService;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

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
        logger.info("Creating order. userId: {}, DTO: {}", userId, orderCreateDTO);
        if (userId == null) {
            logger.warn("Attempt to create order without logging in.");
            return Result.error(401, "请先登录");
        }
        Order order = orderService.createOrder(userId, orderCreateDTO);
        logger.info("Order created: {}", order);
        return Result.success(order);
    }

    @GetMapping("/{id}")
    public Result<OrderInfoDTO> getOrderById(@PathVariable Long id, HttpServletRequest request) {
        logger.info("Getting order by ID: {}", id);
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
        if(userId == null){
            return Result.error(401,"请先登录");
        }
        OrderInfoDTO order = orderService.getOrderById(id);
        logger.info("Returning order: {}", order);
        // 权限校验
        if (!order.getUserId().equals(userId)) {
            return Result.error(403, "无权限");
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
        logger.info("Listing orders for userId: {} with DTO: {}", userId, orderListDTO);
        PageResult<OrderInfoDTO> pageResult = orderService.listOrders(orderListDTO, userId);
        logger.info("Retrieved {} orders for user ID: {}", pageResult.getTotal(), userId);
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
        logger.info("Cancelling order. userId: {}, orderId: {}", userId, id);
        orderService.cancelOrder(id, userId);
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
        logger.info("Attempting to pay for order. userId: {}, orderId: {}", userId, id);
        if (userId == null) {
            logger.warn("Attempt to pay order without logging in.");
            return Result.error(401, "请先登录");
        }
        // 验证权限, 只有订单所有者才能支付
        OrderInfoDTO order = orderService.getOrderById(id);
        if (!order.getUserId().equals(userId)) {
            logger.warn("User {} attempted to pay for order {} without permission.", userId, id);
            return Result.error(403, "无权限支付此订单");
        }
        orderService.payOrder(id); // 这里假设支付成功
        logger.info("Order with ID {} marked as paid.", id);
        return Result.success();
    }
}