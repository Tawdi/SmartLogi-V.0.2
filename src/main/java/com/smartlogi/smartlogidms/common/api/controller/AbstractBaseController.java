package com.smartlogi.smartlogidms.common.api.controller;

import com.smartlogi.smartlogidms.common.api.dto.ApiResponseDTO;
import com.smartlogi.smartlogidms.common.api.dto.ValidationGroups;
import com.smartlogi.smartlogidms.common.domain.BaseEntity;
import com.smartlogi.smartlogidms.common.service.BaseCrudService;
import com.smartlogi.smartlogidms.common.mapper.BaseMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//@Tag(name = "CRUD Operations", description = "Generic CRUD operations for all entities")
public abstract class AbstractBaseController<T extends BaseEntity<ID>, ID, RQ, RS> implements BaseController<T, ID, RQ, RS> {

    protected final BaseCrudService<RQ,RS, ID> service;
    protected final BaseMapper<T, RQ, RS> mapper;

    protected AbstractBaseController(BaseCrudService<RQ,RS, ID> service, BaseMapper<T, RQ, RS> mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Override
    @PostMapping({"","/"})
    @Operation(
            summary = "Create a new resource",
            description = "Creates a new resource with the provided data. All required fields must be provided."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Resource created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Resource already exists")
    })
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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resource updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Resource not found")
    })
    public ResponseEntity<ApiResponseDTO<RS>> update(@PathVariable ID id, @Validated(ValidationGroups.Update.class)  @RequestBody RQ requestDTO) {
        RS responseDTO = service.update(id, requestDTO);
        return ResponseEntity.ok(ApiResponseDTO.success("Resource updated successfully", responseDTO));
    }

    @Override
    @GetMapping("/{id}")
    @Operation(
            summary = "Get resource by ID",
            description = "Retrieves a specific resource by its unique identifier."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resource retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Resource not found")
    })
    public ResponseEntity<ApiResponseDTO<RS>> getById(@PathVariable ID id) {
        RS responseDTO = service.findById(id);
        return ResponseEntity.ok(ApiResponseDTO.success("Resource retrieved successfully", responseDTO));
    }

    @Override
    @GetMapping
    @Operation(
            summary = "Get all resources",
            description = "Retrieves all resources of this type. Use with caution for large datasets."
    )
    @ApiResponse(responseCode = "200", description = "Resources retrieved successfully")
    public ResponseEntity<ApiResponseDTO<List<RS>>> getAll() {
        List<RS> responseDTOs =  service.findAll();
        return ResponseEntity.ok(ApiResponseDTO.success("Resources retrieved successfully", responseDTOs));
    }

    @Override
    @GetMapping("/paginated")
    @Operation(
            summary = "Get paginated resources",
            description = "Retrieves resources with pagination support. Use page, size, and sort parameters for control."
    )
    @ApiResponse(responseCode = "200", description = "Paginated resources retrieved successfully")
    public ResponseEntity<ApiResponseDTO<Page<RS>>> getAllPaginated(@ParameterObject Pageable pageable) {
        Page<RS> responseDTOPage = service.findAll(pageable);
        return ResponseEntity.ok(ApiResponseDTO.success("Resources retrieved successfully", responseDTOPage));
    }

    @Override
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a resource",
            description = "Deletes a specific resource by its unique identifier."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resource deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Resource not found")
    })
    public ResponseEntity<ApiResponseDTO<Void>> delete(@PathVariable ID id) {
        service.deleteById(id);
        return ResponseEntity.ok(ApiResponseDTO.success("Resource deleted successfully", null));
    }
}
