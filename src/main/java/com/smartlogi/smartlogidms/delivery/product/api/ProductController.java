package com.smartlogi.smartlogidms.delivery.product.api;

import com.smartlogi.smartlogidms.common.api.controller.StringBaseController;
import com.smartlogi.smartlogidms.delivery.product.domain.Product;
import com.smartlogi.smartlogidms.delivery.product.service.ProductService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController extends StringBaseController<Product, ProductRequestDTO, ProductResponseDTO> {

    public ProductController(ProductService productService, ProductMapper productMapper) {
        super(productService, productMapper);
    }
}
