package top.aetheria.travelguideplatform.order.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Order {
    private Long id;
    private Long userId;
    private Long productId;
    private LocalDateTime orderTime;
    private Integer quantity;
    private BigDecimal totalPrice;
    private Integer status; // 订单状态 (例如：0-待支付, 1-已支付, 2-已取消, 3-已完成)
    private String paymentMethod;
    private  LocalDateTime paymentTime;
}