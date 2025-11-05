package com.smartlogi.smartlogidms.common.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 *
 * @param <RequestDTO>  Request DTO type
 * @param <ResponseDTO> Request DTO type
 * @param <ID>          ID type
 */
public interface BaseCrudService<T, RequestDTO, ResponseDTO, ID> {

    ResponseDTO save(RequestDTO entity);

    ResponseDTO update(ID id, RequestDTO requestDTO);

    ResponseDTO findById(ID id);

    List<ResponseDTO> findAll();

    Page<ResponseDTO> findAll(Pageable pageable);

    Page<ResponseDTO> findAll(Pageable pageable, Specification<T> spec);

    void deleteById(ID id);

    boolean existsById(ID id);
}