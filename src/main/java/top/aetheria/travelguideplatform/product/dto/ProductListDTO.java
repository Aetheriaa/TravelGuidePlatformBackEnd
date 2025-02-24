package top.aetheria.travelguideplatform.product.dto;

import lombok.Data;

@Data
public class ProductListDTO {
    private Integer page = 1;
    private Integer pageSize = 10;
    private String keyword; // 搜索关键词
    private String type;    // 产品类型
    private String sortBy;  // 排序字段
    private String sortOrder; // 排序方式
}