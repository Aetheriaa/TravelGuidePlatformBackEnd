package top.aetheria.travelguideplatform.order.dto;
import lombok.Data;

@Data
public class OrderListDTO {
    private Integer page = 1;
    private Integer pageSize = 10;
    private String keyword;
    private Long userId;
    // 可以根据需要添加其他筛选条件，例如：
    // private Integer status;
    // private LocalDateTime startTime;
    // private LocalDateTime endTime;
}