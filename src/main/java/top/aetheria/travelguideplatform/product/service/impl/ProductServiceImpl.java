package top.aetheria.travelguideplatform.product.service.impl;

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
import top.aetheria.travelguideplatform.product.dto.ProductCreateDTO;
import top.aetheria.travelguideplatform.product.dto.ProductInfoDTO;
import top.aetheria.travelguideplatform.product.dto.ProductListDTO;
import top.aetheria.travelguideplatform.product.dto.ProductUpdateDTO;
import top.aetheria.travelguideplatform.product.entity.Product;
import top.aetheria.travelguideplatform.product.mapper.ProductMapper;
import top.aetheria.travelguideplatform.product.service.ProductService;
import top.aetheria.travelguideplatform.user.entity.User;
import top.aetheria.travelguideplatform.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private UserMapper userMapper; //注入

    @Override
    @Transactional
    public Product create(ProductCreateDTO productCreateDTO, Long userId) { // 添加 userId 参数
        Product product = new Product();
        BeanUtils.copyProperties(productCreateDTO, product);
        product.setCreateTime(LocalDateTime.now());
        product.setStatus(1); // 默认上架状态
        product.setUserId(userId);  // 设置发布者 ID
        productMapper.insert(product);
        logger.info("Created product: {}", product);
        return product;
    }
    @Override
    public ProductInfoDTO getById(Long id) {
        Product product = productMapper.findById(id);
        if (product == null) {
            logger.warn("Product with ID: {} not found", id);
            throw new BusinessException(404, "产品不存在");
        }
        ProductInfoDTO productInfoDTO = new ProductInfoDTO();
        BeanUtils.copyProperties(product, productInfoDTO);
        // 查询发布者信息
        User user = userMapper.findById(product.getUserId());
        if (user != null) {
            productInfoDTO.setUserId(user.getId());
            // 可以根据需要设置其他用户信息，例如用户名、头像等
        }
        logger.info("Returning product info DTO for ID: {}", id);
        return productInfoDTO;
    }
    @Override
    @Transactional
    public void update(ProductUpdateDTO productUpdateDTO) {
        Product product = productMapper.findById(productUpdateDTO.getId());
        if (product == null) {
            logger.warn("Attempt to update non-existent product with ID: {}", productUpdateDTO.getId());
            throw new BusinessException(404, "产品不存在");
        }
        BeanUtils.copyProperties(productUpdateDTO, product);
        productMapper.update(product);
        logger.info("Updated product with ID: {}", product.getId());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Product product = productMapper.findById(id);
        if(product == null){
            logger.warn("Attempt to delete non-existent product with ID: {}", id);
            throw new BusinessException(404,"产品不存在");
        }
        productMapper.delete(id);
        logger.info("Deleted product with ID: {}", id);
    }

    @Override
    public PageResult<Product> list(ProductListDTO productListDTO) {

        // 设置分页参数
        PageHelper.startPage(productListDTO.getPage(), productListDTO.getPageSize());

        // 执行查询
        List<Product> guides = productMapper.list(productListDTO);
        logger.info("查询数量：{}",guides.size());
        // 获取分页结果
        Page<Product> page = (Page<Product>) guides;

        // 封装 PageResult
        return new PageResult<>(page.getTotal(), page.getResult());
    }
    @Override
    public void setStatus(Long id, Integer status){
        Product product = productMapper.findById(id);
        if (product == null) {
            logger.warn("Attempt to change status of non-existent product with ID: {}", id);
            throw new BusinessException(404, "产品不存在");
        }
        product.setStatus(status);
        productMapper.update(product);
        logger.info("Changed status of product with ID: {} to {}", id,status);
    }
}