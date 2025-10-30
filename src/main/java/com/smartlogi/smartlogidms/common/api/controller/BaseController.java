package com.smartlogi.smartlogidms.common.api.controller;
import com.smartlogi.smartlogidms.common.api.dto.ApiResponse;
import com.smartlogi.smartlogidms.common.domain.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

public interface BaseController<T extends BaseEntity<ID>, ID, RQ, RS> {

    ResponseEntity<ApiResponse<RS>> create(@Valid @RequestBody RQ requestDTO);

    ResponseEntity<ApiResponse<RS>> update(@PathVariable ID id, @Valid @RequestBody RQ requestDTO);

    ResponseEntity<ApiResponse<RS>> getById(@PathVariable ID id);

    ResponseEntity<ApiResponse<List<RS>>> getAll();

    ResponseEntity<ApiResponse<Page<RS>>> getAllPaginated(Pageable pageable);

    ResponseEntity<ApiResponse<Void>> delete(@PathVariable ID id);
}
