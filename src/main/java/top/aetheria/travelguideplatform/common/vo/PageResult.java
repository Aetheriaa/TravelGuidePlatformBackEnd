package top.aetheria.travelguideplatform.common.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 分页查询返回结果
 * @param <T>
 */
@Data
public class PageResult<T> implements Serializable {

    private Long total; //总记录数

    private List<T> records; //当前页数据

    public PageResult(Long total, List<T> records) {
        this.total = total;
        this.records = records;
    }
}
