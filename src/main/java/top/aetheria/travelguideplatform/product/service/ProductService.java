package top.aetheria.travelguideplatform.product.service;

import top.aetheria.travelguideplatform.common.vo.PageResult;
import top.aetheria.travelguideplatform.product.dto.ProductCreateDTO;
import top.aetheria.travelguideplatform.product.dto.ProductInfoDTO;
import top.aetheria.travelguideplatform.product.dto.ProductListDTO;
import top.aetheria.travelguideplatform.product.dto.ProductUpdateDTO;
import top.aetheria.travelguideplatform.product.entity.Product;

public interface ProductService {

    Product create(ProductCreateDTO productCreateDTO, Long userId);

    ProductInfoDTO getById(Long id);

    void update(ProductUpdateDTO productUpdateDTO);

    void delete(Long id);

    PageResult<Product> list(ProductListDTO productListDTO);

    void setStatus(Long id, Integer status);

}