package top.aetheria.travelguideplatform.product.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Product {
    private Long id;
    private String type; // 使用 String 类型，对应 ENUM('flight', 'hotel', 'ticket', 'package')
    private String name;
    private String description;
    private BigDecimal price;
    private String supplier;
    private Integer stock;
    private String image;
    private LocalDateTime createTime;
    private Integer status; // 1: 上架, 0: 下架
    private Long userId; //如果需要记录是谁发布的，添加这一行

}