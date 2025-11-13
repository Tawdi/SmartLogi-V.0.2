package com.smartlogi.smartlogidms.common.api.controller;
import com.smartlogi.smartlogidms.common.api.dto.ApiResponseDTO;
import com.smartlogi.smartlogidms.common.domain.entity.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;


public interface BaseController<T extends BaseEntity<I>, I,R1, R2> {

    ResponseEntity<ApiResponseDTO<R2>> create(@Valid @RequestBody R1 requestDTO);

    ResponseEntity<ApiResponseDTO<R2>> update(@PathVariable I id, @Valid @RequestBody R1 requestDTO);

    ResponseEntity<ApiResponseDTO<R2>> getById(@PathVariable I id);

    ResponseEntity<ApiResponseDTO<List<R2>>> getAll();

    ResponseEntity<ApiResponseDTO<Page<R2>>> getAllPaginated(Pageable pageable, MultiValueMap<String,String> filters);

    ResponseEntity<ApiResponseDTO<Void>> delete(@PathVariable I id);
}
