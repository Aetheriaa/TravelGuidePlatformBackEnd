package top.aetheria.travelguideplatform.product.controller;

        import jakarta.servlet.http.HttpServletRequest;
        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private JwtUtils jwtUtils;

    // 创建产品 (任何人都可以创建)
    @PostMapping
    public Result<Product> create(@Validated @RequestBody ProductCreateDTO productCreateDTO, HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token != null && token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
        logger.info("Creating product. userId: {}, DTO: {}", userId, productCreateDTO);
        if (userId == null) {
            return Result.error(401, "未登录或登录已过期");
        }
        Product product = productService.create(productCreateDTO,userId); // 传入userId
        logger.info("Product created: {}", product);
        return Result.success(product);
    }

    @GetMapping("/{id}")
    public Result<ProductInfoDTO> getById(@PathVariable Long id) {
        logger.info("Getting product by ID: {}", id);
        ProductInfoDTO product = productService.getById(id);
        logger.info("Returning product: {}", product);
        return Result.success(product);
    }
    //更新
    @PutMapping
    public Result update(@Validated @RequestBody ProductUpdateDTO productUpdateDTO,HttpServletRequest request) {
        // 1. 从请求头中获取 token，并解析出 userId
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token == null || !token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            logger.warn("Attempt to update product without logging in.");
            return Result.error(401, "未登录或登录已过期");
        }
        token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        Long userId = jwtUtils.getUserIdFromToken(token);
        if (userId == null) {
            logger.warn("Attempt to update product with invalid token.");
            return Result.error(401, "未登录或登录已过期");
        }

        // 2. 查询要更新的产品
        logger.info("Updating product. userId: {}, DTO: {}", userId, productUpdateDTO);
        ProductInfoDTO product = productService.getById(productUpdateDTO.getId());
        if (product == null) {
            logger.warn("Attempt to update non-existent product with ID: {}", productUpdateDTO.getId());
            return Result.error(404, "产品不存在");
        }

        // 3. 检查权限：只有发布者才能修改
        if (!userId.equals(product.getUserId())) {
            logger.warn("User {} attempted to update product {} without permission.", userId, productUpdateDTO.getId());
            return Result.error(403, "无权限修改此产品");
        }

        // 4. 执行更新
        productService.update(productUpdateDTO);
        logger.info("Product with ID {} updated successfully.", productUpdateDTO.getId());
        return Result.success();
    }

    //上下架
    @PutMapping("/{id}/{status}")
    public Result updateStatus(@PathVariable Long id, @PathVariable Integer status, HttpServletRequest request) {
        // 1. 从请求头中获取 token，并解析出 userId
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);
        if (token == null || !token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            logger.warn("Attempt to update product status without logging in.");
            return Result.error(401, "未登录或登录已过期");
        }
        token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        Long userId = jwtUtils.getUserIdFromToken(token);
        if (userId == null) {
            logger.warn("Attempt to update product status with invalid token.");
            return Result.error(401, "未登录或登录已过期");
        }

        // 2. 查询要更新的产品
        logger.info("Updating product status. userId: {}, productId: {}, status: {}", userId, id, status);
        ProductInfoDTO product = productService.getById(id);
        if (product == null) {
            logger.warn("Attempt to update status of non-existent product with ID: {}", id);
            return Result.error(404, "产品不存在");
        }

        // 3. 检查权限：只有发布者才能修改
        if (!userId.equals(product.getUserId())) {
            logger.warn("User {} attempted to update status of product {} without permission.", userId, id);
            return Result.error(403, "无权限修改此产品");
        }

        // 4. 验证状态值是否合法 (可选)
        if (status != 0 && status != 1) {
            logger.warn("Invalid status value: {} for product ID: {}", status, id);
            return Result.error(400, "无效的状态值");
        }
        productService.setStatus(id, status);
        logger.info("Product with ID {} status updated to {}.", id, status);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id, HttpServletRequest request) {
        // 1. 从请求头中获取 token，并解析出 userId
        String token = request.getHeader(AppConstants.JWT_HEADER_KEY);

        if (token == null || !token.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            logger.warn("Attempt to delete product without logging in.");
            return Result.error(401, "未登录或登录已过期");
        }
        token = token.substring(AppConstants.JWT_TOKEN_PREFIX.length());
        Long userId = jwtUtils.getUserIdFromToken(token);
        if (userId == null) {
            logger.warn("Attempt to delete product with invalid token.");
            return Result.error(401, "未登录或登录已过期");
        }

        // 2. 查询要删除的产品
        logger.info("Deleting product. userId: {}, productId: {}", userId, id);
        ProductInfoDTO product = productService.getById(id);
        if (product == null) {
            logger.warn("Attempt to delete non-existent product with ID: {}", id);
            return Result.error(404, "产品不存在");
        }

        // 3. 检查权限：只有发布者才能删除
        if (!userId.equals(product.getUserId())) {
            logger.warn("User {} attempted to delete product {} without permission.", userId, id);
            return Result.error(403, "无权限删除此产品");
        }

        // 4. 执行删除
        productService.delete(id);
        logger.info("Product with ID {} deleted successfully.", id);
        return Result.success();
    }

    @GetMapping
    public Result<PageResult<Product>> list(ProductListDTO productListDTO) {
        logger.info("Listing products with DTO: {}", productListDTO);
        PageResult<Product> pageResult = productService.list(productListDTO);
        logger.info("Retrieved {} products.", pageResult.getTotal());
        return Result.success(pageResult);
    }
}