package top.aetheria.travelguideplatform.guide.dto;

import lombok.Data;

import java.util.List;

@Data
public class GuideListDTO {
    private String keyword; // 搜索关键词
//    private String tag;    // 标签筛选
    private List<String> tags;    // 标签筛选
    private Integer page = 1;       // 当前页码，默认第1页
    private Integer pageSize = 10;   // 每页大小，默认10条
    private String sortBy;   //排序字段
    private  String sortOrder; //排序方式
}
