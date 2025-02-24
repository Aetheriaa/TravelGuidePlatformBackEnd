package top.aetheria.travelguideplatform.product.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductInfoDTO {
    private Long id;
    private String type;
    private String name;
    private String description;
    private BigDecimal price;
    private String supplier;
    private Integer stock;
    private String image;
    private LocalDateTime createTime;
    private Integer status;
    private Long userId;
}