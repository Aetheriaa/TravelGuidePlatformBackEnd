package top.aetheria.travelguideplatform.product.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class ProductCreateDTO {

    @NotBlank(message = "产品类型不能为空")
    private String type;

    @NotBlank(message = "产品名称不能为空")
    private String name;

    private String description; // 产品描述，可选

    @NotNull(message = "产品价格不能为空")
    private BigDecimal price;

    private String supplier; // 供应商，可选

    @NotNull(message = "产品库存不能为空")
    private Integer stock;

    private String image; // 产品图片，可选
}