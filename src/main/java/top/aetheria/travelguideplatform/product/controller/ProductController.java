package top.aetheria.travelguideplatform.product.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.aetheria.travelguideplatform.common.constant.AppConstants;
import top.aetheria.travelguideplatform.common.exception.BusinessException;
import top.aetheria.travelguideplatform.common.utils.JwtUtils;
import top.aetheria.travelguideplatform.common.vo.PageResult;
import top.aetheria.travelguideplatform.common.vo.Result;
import top.aetheria.travelguideplatform.product.dto.ProductCreateDTO;
import top.aetheria.travelguideplatform.product.dto.ProductInfoDTO;
import top.aetheria.travelguideplatform.product.dto.ProductListDTO;
import top.aetheria.travelguideplatform.product.dto.ProductUpdateDTO;
import top.aetheria.travelguideplatform.product.entity.Product;
import top.aetheria.travelguideplatform.product.service.ProductService;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private JwtUtils jwtUtils;

    // 创建产品 (任何人都可以创建)
//    @PostMapping
//    public Result<Product> create(@Validated @RequestBody ProductCreateDTO productCreateDTO) {
//        // 注意：这里不再需要从 JWT 中获取 userId，因为任何人都可以创建
//        Product product = productService.create(productCreateDTO);
//        return Result.success(product);
//    }
    @PostMapping
    public Result<Product> create(@Validated @RequestBody ProductCreateDTO productCreateDTO, HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未登录或登录已过期");
        }
        Product product = productService.create(productCreateDTO,userId); // 传入userId
        return Result.success(product);
    }

    @GetMapping("/{id}")
    public Result<ProductInfoDTO> getById(@PathVariable Long id) {
        ProductInfoDTO product = productService.getById(id);
        return Result.success(product);
    }
    //更新
    @PutMapping
    public Result update(@Validated @RequestBody ProductUpdateDTO productUpdateDTO, HttpServletRequest request) {
        // 1. 从请求头中获取 token，并解析出 userId
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token == null || !token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            return Result.error(401, "未登录或登录已过期");
        }
        token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        Long userId = jwtUtils.getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未登录或登录已过期");
        }

        // 2. 查询要更新的产品
        ProductInfoDTO product = productService.getById(productUpdateDTO.getId());
        if (product == null) {
            return Result.error(404, "产品不存在");
        }

        // 3. 检查权限：只有发布者才能修改
        if (!userId.equals(product.getUserId())) {
            return Result.error(403, "无权限修改此产品");
        }

        // 4. 执行更新
        productService.update(productUpdateDTO);
        return Result.success();
    }

    //上下架
    @PutMapping("/{id}/{status}")
    public Result updateStatus(@PathVariable Long id, @PathVariable Integer status, HttpServletRequest request) {
        // 1. 从请求头中获取 token，并解析出 userId
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token == null || !token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            return Result.error(401, "未登录或登录已过期");
        }
        token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        Long userId = jwtUtils.getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未登录或登录已过期");
        }

        // 2. 查询要更新的产品
        ProductInfoDTO product = productService.getById(id);
        if (product == null) {
            return Result.error(404, "产品不存在");
        }

        // 3. 检查权限：只有发布者才能修改
        if (!userId.equals(product.getUserId())) {
            return Result.error(403, "无权限修改此产品");
        }

        // 4. 验证状态值是否合法 (可选)
        if (status != 0 && status != 1) {
            return Result.error(400, "无效的状态值");
        }
        productService.setStatus(id, status);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id, HttpServletRequest request) {
        // 1. 从请求头中获取 token，并解析出 userId
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);

        if (token == null || !token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            return Result.error(401, "未登录或登录已过期");
        }
        token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        Long userId = jwtUtils.getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未登录或登录已过期");
        }

        // 2. 查询要删除的产品
        ProductInfoDTO product = productService.getById(id);
        if (product == null) {
            return Result.error(404, "产品不存在");
        }

        // 3. 检查权限：只有发布者才能删除
        if (!userId.equals(product.getUserId())) {
            return Result.error(403, "无权限删除此产品");
        }

        // 4. 执行删除
        productService.delete(id);
        return Result.success();
    }

    @GetMapping
    public Result<PageResult<Product>> list(ProductListDTO productListDTO) {
        PageResult<Product> pageResult = productService.list(productListDTO);
        return Result.success(pageResult);
    }
}