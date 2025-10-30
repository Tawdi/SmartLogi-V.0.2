package com.smartlogi.smartlogidms.common.api.controller;

import com.smartlogi.smartlogidms.common.api.dto.ApiResponse;
import com.smartlogi.smartlogidms.common.domain.BaseEntity;
import com.smartlogi.smartlogidms.common.service.BaseCrudService;
import com.smartlogi.smartlogidms.common.mapper.BaseMapper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public abstract class AbstractBaseController<T extends BaseEntity<ID>, ID, RQ, RS> implements BaseController<T, ID, RQ, RS> {

    protected final BaseCrudService<RQ,RS, ID> service;
    protected final BaseMapper<T, RQ, RS> mapper;

    protected AbstractBaseController(BaseCrudService<RQ,RS, ID> service, BaseMapper<T, RQ, RS> mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Override
    @PostMapping({"","/"})
    public ResponseEntity<ApiResponse<RS>> create(@Valid @RequestBody RQ requestDTO) {

        RS responseDTO = service.save(requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Resource created successfully", responseDTO));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RS>> update(@PathVariable ID id, @Valid @RequestBody RQ requestDTO) {
        RS responseDTO = service.update(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success("Resource updated successfully", responseDTO));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RS>> getById(@PathVariable ID id) {
        RS responseDTO = service.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Resource retrieved successfully", responseDTO));
    }

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<List<RS>>> getAll() {
        List<RS> responseDTOs =  service.findAll();
        return ResponseEntity.ok(ApiResponse.success("Resources retrieved successfully", responseDTOs));
    }

    @Override
    @GetMapping("/paginated")
    public ResponseEntity<ApiResponse<Page<RS>>> getAllPaginated(Pageable pageable) {
        Page<RS> responseDTOPage = service.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success("Resources retrieved successfully", responseDTOPage));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable ID id) {
        service.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Resource deleted successfully", null));
    }
}
