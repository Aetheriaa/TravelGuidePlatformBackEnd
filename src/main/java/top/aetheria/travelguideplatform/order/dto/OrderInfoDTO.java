package top.aetheria.travelguideplatform.order.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderInfoDTO {
    private Long id;
    private Long userId;
    private String username; // 用户名
    private Long productId;
    private String productName; // 产品名称
    private String productImage; //产品图片
    private LocalDateTime orderTime;
    private Integer quantity;
    private BigDecimal totalPrice;
    private Integer status;
    private String paymentMethod;
    private LocalDateTime paymentTime;
}