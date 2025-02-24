package top.aetheria.travelguideplatform.product.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class ProductUpdateDTO {

    @NotNull(message = "产品ID不能为空")
    private Long id;

    private String type; // 可选
    private String name; // 可选
    private String description;
    private BigDecimal price;
    private String supplier;
    private Integer stock;
    private String image;
    private Integer status; // 可选，用于上架/下架
}