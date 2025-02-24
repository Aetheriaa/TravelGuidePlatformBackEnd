package top.aetheria.travelguideplatform.order.mapper;

import org.apache.ibatis.annotations.*;
import top.aetheria.travelguideplatform.order.dto.OrderInfoDTO;
import top.aetheria.travelguideplatform.order.dto.OrderListDTO;
import top.aetheria.travelguideplatform.order.entity.Order;

import java.util.List;

@Mapper
public interface OrderMapper {

    @Insert("INSERT INTO orders (user_id, product_id, order_time, quantity, total_price, status, payment_method) " +
            "VALUES (#{userId}, #{productId}, #{orderTime}, #{quantity}, #{totalPrice}, #{status}, #{paymentMethod})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Order order);

    @Select("SELECT * FROM orders WHERE id = #{id}")
    Order findById(Long id);

    @Select("SELECT * FROM orders WHERE user_id = #{userId} ORDER BY order_time DESC")
    List<Order> findByUserId(Long userId);

    @Update("UPDATE orders SET status = #{status} WHERE id = #{id}")
    void updateStatus(@Param("id") Long id, @Param("status") Integer status);

    // 订单列表查询（支持分页、关键词搜索等）
//    List<Order> list(OrderListDTO orderListDTO);
    List<OrderInfoDTO> list(OrderListDTO orderListDTO);



    Long count(OrderListDTO orderListDTO);

    @Update("<script>" +
            "UPDATE orders " +
            "<set>" +
            "  <if test='userId != null'>user_id = #{userId},</if>" +
            "  <if test='productId != null'>product_id = #{productId},</if>" +
            "  <if test='orderTime != null'>order_time = #{orderTime},</if>" +
            "  <if test='quantity != null'>quantity = #{quantity},</if>" +
            "  <if test='totalPrice != null'>total_price = #{totalPrice},</if>" +
            "  <if test='status != null'>status = #{status},</if>" +
            "  <if test='paymentMethod != null'>payment_method = #{paymentMethod},</if>" +
            "  <if test='paymentTime != null'>payment_time = #{paymentTime},</if>" +
            "</set>" +
            "WHERE id = #{id}" +
            "</script>")
    void update(Order order);


}
