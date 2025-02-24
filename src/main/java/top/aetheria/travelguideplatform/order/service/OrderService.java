package top.aetheria.travelguideplatform.order.service;

import top.aetheria.travelguideplatform.common.vo.PageResult;
import top.aetheria.travelguideplatform.order.dto.OrderCreateDTO;
import top.aetheria.travelguideplatform.order.dto.OrderInfoDTO;
import top.aetheria.travelguideplatform.order.dto.OrderListDTO;
import top.aetheria.travelguideplatform.order.entity.Order;

public interface OrderService {
    Order createOrder(Long userId, OrderCreateDTO orderCreateDTO);

    OrderInfoDTO getOrderById(Long orderId);

    PageResult<OrderInfoDTO> listOrders(OrderListDTO orderListDTO,Long userId);

    void cancelOrder(Long orderId,Long userId);

    void payOrder(Long id); // 支付订单

    // 支付接口 (可选)
    // String createPayment(Long orderId, String paymentMethod);
}