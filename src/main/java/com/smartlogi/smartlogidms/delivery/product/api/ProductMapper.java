package com.smartlogi.smartlogidms.delivery.product.api;

import com.smartlogi.smartlogidms.delivery.product.domain.Product;
import org.mapstruct.*;


@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toEntity(ProductRequestDTO dto);

    ProductRequestDTO toDto(Product entity);
}