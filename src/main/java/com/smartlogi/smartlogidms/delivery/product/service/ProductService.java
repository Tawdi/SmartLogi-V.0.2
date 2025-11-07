package com.smartlogi.smartlogidms.delivery.product.service;

import com.smartlogi.smartlogidms.common.service.StringCrudService;
import com.smartlogi.smartlogidms.delivery.product.api.ProductRequestDTO;
import com.smartlogi.smartlogidms.delivery.product.api.ProductResponseDTO;
import com.smartlogi.smartlogidms.delivery.product.domain.Product;

public interface ProductService extends StringCrudService<Product, ProductRequestDTO, ProductResponseDTO> {
}
