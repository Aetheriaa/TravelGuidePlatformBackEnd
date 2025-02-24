package top.aetheria.travelguideplatform.guide.entity;

import lombok.Data;

@Data
public class Tag {
    private Long id;
    private String name;
    private Integer popularity; //热度, 可选
}