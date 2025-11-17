package com.smartlogi.smartlogidms.delivery.product.service;

import com.smartlogi.smartlogidms.common.exception.ResourceNotFoundException;
import com.smartlogi.smartlogidms.delivery.product.api.ProductMapper;
import com.smartlogi.smartlogidms.delivery.product.api.ProductMapperImpl;
import com.smartlogi.smartlogidms.delivery.product.api.ProductRequestDTO;
import com.smartlogi.smartlogidms.delivery.product.api.ProductResponseDTO;
import com.smartlogi.smartlogidms.delivery.product.domain.Product;
import com.smartlogi.smartlogidms.delivery.product.domain.ProductRrepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRrepository repository;

    private ProductMapper mapper;
    private ProductServiceImpl service;

    private Product entity;
    private ProductRequestDTO request;
    private ProductResponseDTO response;

    @BeforeEach
    void setup() {
        mapper = new ProductMapperImpl();
        service = new ProductServiceImpl(repository, mapper);

        entity = new Product();
        entity.setId("PRD-345");
        entity.setNom("Batman Car");
        entity.setCategorie("TOYS");
        entity.setPrix(8.0);
        entity.setPoids(0.5);

        request = new ProductRequestDTO();
        request.setNom("Batman Car");
        request.setCategorie("TOYS");
        request.setPrix(8.0);
        request.setPoids(0.5);

        response = new ProductResponseDTO();
        response.setId("PRD-345");
        response.setNom("Batman Car");
        response.setCategorie("TOYS");
        response.setPrix(8.0);
        response.setPoids(0.5);
    }

    @Test
    void shouldSaveProductSuccessfully() {
        // Given
        when(repository.save(any(Product.class))).thenReturn(entity);

        // When
        ProductResponseDTO result = service.save(request);

        // Then
        assertNotNull(result);
        assertEquals("PRD-345", result.getId());
        assertEquals("Batman Car", result.getNom());
        assertEquals("TOYS", result.getCategorie());
        assertEquals(8.0, result.getPrix());

        verify(repository, times(1)).save(any(Product.class));
    }

    @Test
    void shouldUpdateProductSuccessfully() {
        // Given
        String productId = "PRD-333";
        when(repository.findById(productId)).thenReturn(Optional.of(entity));
        when(repository.save(any(Product.class))).thenReturn(entity);

        // When
        ProductResponseDTO result = service.update(productId, request);

        // Then
        assertNotNull(result);
        assertEquals("PRD-345", result.getId());
        assertEquals("Batman Car", result.getNom());

        verify(repository, times(1)).findById(productId);
        verify(repository, times(1)).save(any(Product.class));
    }

    @Test
    void shouldFindProductByIdSuccessfully() {
        // Given
        String productId = "PRD-345";
        when(repository.findById(productId)).thenReturn(Optional.of(entity));

        // When
        ProductResponseDTO result = service.findById(productId);

        // Then
        assertNotNull(result);
        assertEquals("PRD-345", result.getId());
        assertEquals("Batman Car", result.getNom());

        verify(repository, times(1)).findById(productId);
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        // Given
        String productId = "NON-EXISTENT";
        when(repository.findById(productId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.findById(productId)
        );

        assertEquals("Resource not found with id: " + productId, exception.getMessage());
        verify(repository, times(1)).findById(productId);
    }

    @Test
    void shouldFindAllProductsSuccessfully() {
        // Given
        List<Product> products = Arrays.asList(entity, createAnotherProduct());
        when(repository.findAll()).thenReturn(products);

        // When
        List<ProductResponseDTO> result = service.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void shouldFindAllProductsWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = Arrays.asList(entity, createAnotherProduct());
        Page<Product> productPage = new PageImpl<>(products, pageable, products.size());
        when(repository.findAll(pageable)).thenReturn(productPage);

        // When
        Page<ProductResponseDTO> result = service.findAll(pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(repository, times(1)).findAll(pageable);
    }

    @Test
    void shouldFindAllProductsWithSpecificationAndPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = Arrays.asList(entity);
        Page<Product> productPage = new PageImpl<>(products, pageable, products.size());
        when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(productPage);

        // When
        Page<ProductResponseDTO> result = service.findAll(pageable, mock(Specification.class));

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(repository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void shouldDeleteProductSuccessfully() {
        // Given
        String productId = "PRD-345";
        when(repository.existsById(productId)).thenReturn(true);
        doNothing().when(repository).deleteById(productId);

        // When
        service.deleteById(productId);

        // Then
        verify(repository, times(1)).existsById(productId);
        verify(repository, times(1)).deleteById(productId);
    }

    @Test
    void shouldCheckIfProductExists() {
        // Given
        String productId = "PRD-345";
        when(repository.existsById(productId)).thenReturn(true);

        // When
        boolean exists = service.existsById(productId);

        // Then
        assertTrue(exists);
        verify(repository, times(1)).existsById(productId);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentProduct() {
        // Given
        String productId = "NON-EXISTENT";
        when(repository.findById(productId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.update(productId, request)
        );

        assertEquals("Resource not found with id: " + productId, exception.getMessage());
        verify(repository, times(1)).findById(productId);
        verify(repository, never()).save(any(Product.class));
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentProduct() {
        // Given
        String productId = "NON-EXISTENT";
        when(repository.existsById(productId)).thenReturn(false);

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.deleteById(productId)
        );

        assertEquals("Resource not found with id: " + productId, exception.getMessage());
        verify(repository, times(1)).existsById(productId);
        verify(repository, never()).deleteById(anyString());
    }

    @Test
    void shouldHandleEmptyProductList() {
        // Given
        when(repository.findAll()).thenReturn(Arrays.asList());

        // When
        List<ProductResponseDTO> result = service.findAll();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository, times(1)).findAll();
    }

    private Product createAnotherProduct() {
        Product anotherProduct = new Product();
        anotherProduct.setId("PRD-346");
        anotherProduct.setNom("Superman Figure");
        anotherProduct.setCategorie("TOYS");
        anotherProduct.setPrix(12.0);
        anotherProduct.setPoids(0.3);
        return anotherProduct;
    }
}