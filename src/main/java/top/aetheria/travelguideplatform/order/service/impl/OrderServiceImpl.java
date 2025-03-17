package top.aetheria.travelguideplatform.order.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.aetheria.travelguideplatform.common.exception.BusinessException;
import top.aetheria.travelguideplatform.common.vo.PageResult;
import top.aetheria.travelguideplatform.order.dto.OrderCreateDTO;
import top.aetheria.travelguideplatform.order.dto.OrderInfoDTO;
import top.aetheria.travelguideplatform.order.dto.OrderListDTO;
import top.aetheria.travelguideplatform.order.entity.Order;
import top.aetheria.travelguideplatform.order.mapper.OrderMapper;
import top.aetheria.travelguideplatform.order.service.OrderService;
import top.aetheria.travelguideplatform.product.entity.Product;
import top.aetheria.travelguideplatform.product.mapper.ProductMapper;
import top.aetheria.travelguideplatform.user.entity.User;
import top.aetheria.travelguideplatform.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public Order createOrder(Long userId, OrderCreateDTO orderCreateDTO) {
        // 1. 查询产品信息
        Product product = productMapper.findById(orderCreateDTO.getProductId());
        if (product == null) {
            logger.warn("Attempt to create order with non-existent product ID: {}", orderCreateDTO.getProductId());
            throw new BusinessException(404, "产品不存在");
        }

        // 2. 检查库存
        if (product.getStock() < orderCreateDTO.getQuantity()) {
            logger.warn("Attempt to order product with insufficient stock. Product ID: {}, requested quantity: {}, available stock: {}",
                    product.getId(), orderCreateDTO.getQuantity(), product.getStock());
            throw new BusinessException(400, "库存不足");
        }

        // 3. 创建订单对象
        Order order = new Order();
        order.setUserId(userId);
        order.setProductId(orderCreateDTO.getProductId());
        order.setOrderTime(LocalDateTime.now());
        order.setQuantity(orderCreateDTO.getQuantity());
        order.setTotalPrice(product.getPrice().multiply(new java.math.BigDecimal(orderCreateDTO.getQuantity())));
        order.setStatus(0); // 0 表示待支付

        // 4. 插入订单数据
        orderMapper.insert(order);
        logger.info("Order created: {}", order);
        // 5. 扣减库存 (可选)
        // product.setStock(product.getStock() - orderCreateDTO.getQuantity());
        // productMapper.update(product);

        return order;
    }

    @Override
    public OrderInfoDTO getOrderById(Long orderId) {
        // 1. 查询订单信息
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            logger.warn("Order with ID: {} not found", orderId);
            throw new BusinessException(404, "订单不存在");
        }

        // 2. 查询用户信息
        User user = userMapper.findById(order.getUserId());

        // 3. 查询产品信息
        Product product = productMapper.findById(order.getProductId());

        // 4. 组装 OrderInfoDTO
        OrderInfoDTO orderInfoDTO = new OrderInfoDTO();
        BeanUtils.copyProperties(order, orderInfoDTO);
        if (user != null) {
            orderInfoDTO.setUsername(user.getUsername());
        }
        if (product != null) {
            orderInfoDTO.setProductName(product.getName());
            orderInfoDTO.setProductImage(product.getImage());
        }
        logger.info("Returning order info DTO for order ID: {}", orderId);
        return orderInfoDTO;
    }

    @Override
    public PageResult<OrderInfoDTO> listOrders(OrderListDTO orderListDTO,Long userId) {
        // 设置分页参数
        PageHelper.startPage(orderListDTO.getPage(), orderListDTO.getPageSize());
        // 添加 userId 到查询条件
        orderListDTO.setUserId(userId);
        // 执行查询
        List<OrderInfoDTO> orders = orderMapper.list(orderListDTO);
        logger.info("查询数量：{}",orders.size());
        // 获取分页结果
        Page<OrderInfoDTO> page = (Page<OrderInfoDTO>) orders;

        // 封装 PageResult
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId,Long userId) {
        // 1. 查询订单信息
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            logger.warn("Attempt to cancel non-existent order with ID: {}", orderId);
            throw new BusinessException(404, "订单不存在");
        }

        // 2. 检查订单状态 (只有待支付的订单才能取消)
        if (order.getStatus() != 0) {
            logger.warn("Attempt to cancel order with ID: {} that is not in pending payment status.", orderId);
            throw new BusinessException(400, "订单状态不允许取消");
        }
        // 3. 检查权限
        if(!order.getUserId().equals(userId)){
            logger.warn("Attempt to cancel order with ID: {} by unauthorized user ID: {}.", orderId, userId);
            throw new BusinessException(403,"无权限");
        }
        // 4. 更新订单状态为已取消
        orderMapper.updateStatus(orderId, 2); // 2 表示已取消
        logger.info("Order with ID: {} canceled successfully.", orderId);
        //  5. 恢复库存 (可选)
        Product product = productMapper.findById(order.getProductId());
        product.setStock(product.getStock() + order.getQuantity());
        productMapper.update(product);
    }
    @Override
    @Transactional
    public void payOrder(Long orderId) {
        // 1. 查询订单信息
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            logger.warn("Attempt to pay for non-existent order with ID: {}", orderId);
            throw new BusinessException(404, "订单不存在");
        }
        // 2. 检查订单状态 (只有待支付的订单才能支付)
        if (order.getStatus() != 0) { // 0 表示待支付
            logger.warn("Attempt to pay for order with ID: {} that is not in pending payment status.", orderId);
            throw new BusinessException(400, "订单状态不允许支付");
        }

        // 3. 更新订单状态为已支付, 并设置支付时间
        order.setStatus(1); // 1 表示已支付
        order.setPaymentTime(LocalDateTime.now());
        orderMapper.update(order); //  更新整个 order 对象
    }
    // 支付接口 (可选, 仅提供基本思路)
    // public String createPayment(Long orderId, String paymentMethod) {
    //     // 1. 查询订单信息
    //     // 2. 检查订单状态 (只有待支付的订单才能支付)
    //     // 3. 调用第三方支付接口 (例如支付宝、微信支付)
    //     // 4. 生成支付链接或二维码
    //     // 5. 返回支付链接或二维码给前端
    // }
}