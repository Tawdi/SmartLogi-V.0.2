package com.smartlogi.smartlogidms.delivery.product.service;

import com.smartlogi.smartlogidms.common.service.implementation.StringCrudServiceImpl;
import com.smartlogi.smartlogidms.delivery.product.api.ProductMapper;
import com.smartlogi.smartlogidms.delivery.product.api.ProductRequestDTO;
import com.smartlogi.smartlogidms.delivery.product.api.ProductResponseDTO;
import com.smartlogi.smartlogidms.delivery.product.domain.Product;
import com.smartlogi.smartlogidms.delivery.product.domain.ProductRrepository;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl extends StringCrudServiceImpl<Product, ProductRequestDTO, ProductResponseDTO> implements ProductService {

//    private final ProductRrepository productRrepo;
//    private final ProductMapper productMapper;
//
    public ProductServiceImpl(ProductRrepository productRrepo,ProductMapper productMapper){
        super(productRrepo,productMapper);
    }
}
