package top.aetheria.travelguideplatform.topic.dto;

import lombok.Data;

@Data
public class TopicListDTO {
    private String keyword;
    private  String sortBy;
    private String sortOrder;
    private Integer page = 1;
    private Integer pageSize = 10;
}