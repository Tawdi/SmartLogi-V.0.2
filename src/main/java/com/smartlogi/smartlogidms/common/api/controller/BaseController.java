package com.smartlogi.smartlogidms.common.api.controller;
import com.smartlogi.smartlogidms.common.api.dto.ApiResponseDTO;
import com.smartlogi.smartlogidms.common.domain.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

public interface BaseController<T extends BaseEntity<ID>, ID, RQ, RS> {

    ResponseEntity<ApiResponseDTO<RS>> create(@Valid @RequestBody RQ requestDTO);

    ResponseEntity<ApiResponseDTO<RS>> update(@PathVariable ID id, @Valid @RequestBody RQ requestDTO);

    ResponseEntity<ApiResponseDTO<RS>> getById(@PathVariable ID id);

    ResponseEntity<ApiResponseDTO<List<RS>>> getAll();

    ResponseEntity<ApiResponseDTO<Page<RS>>> getAllPaginated(Pageable pageable);

    ResponseEntity<ApiResponseDTO<Void>> delete(@PathVariable ID id);
}
