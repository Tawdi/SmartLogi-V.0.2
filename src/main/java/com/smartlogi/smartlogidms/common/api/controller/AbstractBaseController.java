package com.smartlogi.smartlogidms.common.api.controller;

import com.smartlogi.smartlogidms.common.api.dto.ApiResponseDTO;
import com.smartlogi.smartlogidms.common.api.dto.ValidationGroups;
import com.smartlogi.smartlogidms.common.domain.entity.BaseEntity;
import com.smartlogi.smartlogidms.common.service.BaseCrudService;
import com.smartlogi.smartlogidms.common.mapper.BaseMapper;
import com.smartlogi.smartlogidms.common.specification.FilterParser;
import com.smartlogi.smartlogidms.common.specification.GenericSpecification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class AbstractBaseController<T extends BaseEntity<ID>, ID, RQ, RS> implements BaseController<T, ID, RQ, RS> {

    protected final BaseCrudService<T, RQ, RS, ID> service;
    protected final BaseMapper<T, RQ, RS> mapper;

    protected AbstractBaseController(BaseCrudService<T, RQ, RS, ID> service, BaseMapper<T, RQ, RS> mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Override
    @PostMapping({"", "/"})
    @Operation(
            summary = "Create a new resource",
            description = "Creates a new resource with the provided data. All required fields must be provided."
    )

    @ApiResponse(responseCode = "201", description = "Resource created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "409", description = "Resource already exists")

    public ResponseEntity<ApiResponseDTO<RS>> create(@Validated(ValidationGroups.Create.class) @RequestBody RQ requestDTO) {

        RS responseDTO = service.save(requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success("Resource created successfully", responseDTO));
    }

    @Override
    @PutMapping("/{id}")
    @Operation(
            summary = "Update an existing resource",
            description = "Updates an existing resource with the provided data. Only provided fields will be updated."
    )
    @ApiResponse(responseCode = "200", description = "Resource updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "404", description = "Resource not found")

    public ResponseEntity<ApiResponseDTO<RS>> update(@PathVariable ID id, @Validated(ValidationGroups.Update.class) @RequestBody RQ requestDTO) {
        RS responseDTO = service.update(id, requestDTO);
        return ResponseEntity.ok(ApiResponseDTO.success("Resource updated successfully", responseDTO));
    }

    @Override
    @GetMapping("/{id}")
    @Operation(
            summary = "Get resource by ID",
            description = "Retrieves a specific resource by its unique identifier."
    )

    @ApiResponse(responseCode = "200", description = "Resource retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Resource not found")

    public ResponseEntity<ApiResponseDTO<RS>> getById(@PathVariable ID id) {
        RS responseDTO = service.findById(id);
        return ResponseEntity.ok(ApiResponseDTO.success("Resource retrieved successfully", responseDTO));
    }

    @Override
    @GetMapping("/no-pagination")
//    @GetMapping
    @Operation(
            summary = "Get all resources",
            description = "Retrieves all resources of this type. Use with caution for large datasets."
    )
    @ApiResponse(responseCode = "200", description = "Resources retrieved successfully")
    public ResponseEntity<ApiResponseDTO<List<RS>>> getAll() {
        List<RS> responseDTOs = service.findAll();
        return ResponseEntity.ok(ApiResponseDTO.success("Resources retrieved successfully", responseDTOs));
    }

    @Override
    @GetMapping
//    @GetMapping("/paginated")
    @Operation(
            summary = "Get paginated resources",
            description = "Retrieves resources with pagination support. Use page, size, and sort parameters for control."
    )
    @ApiResponse(responseCode = "200", description = "Paginated resources retrieved successfully")
    public ResponseEntity<ApiResponseDTO<Page<RS>>> getAllPaginated(
            @ParameterObject Pageable pageable,
            @RequestParam(required = false) MultiValueMap<String, String> filters
    ) {
        Class<T> entityClass = getEntityClass();
        GenericSpecification<T> spec = FilterParser.parse(filters,entityClass);

        Page<RS> responseDTOPage = service.findAll(pageable, spec);
        return ResponseEntity.ok(ApiResponseDTO.success("Resources retrieved successfully", responseDTOPage));
    }

    @SuppressWarnings("unchecked")
    private Class<T> getEntityClass() {
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<T>) type.getActualTypeArguments()[0];
    }
    @Override
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a resource",
            description = "Deletes a specific resource by its unique identifier."
    )
    @ApiResponse(responseCode = "200", description = "Resource deleted successfully")
    @ApiResponse(responseCode = "404", description = "Resource not found")

    public ResponseEntity<ApiResponseDTO<Void>> delete(@PathVariable ID id) {
        service.deleteById(id);
        return ResponseEntity.ok(ApiResponseDTO.success("Resource deleted successfully", null));
    }
}
