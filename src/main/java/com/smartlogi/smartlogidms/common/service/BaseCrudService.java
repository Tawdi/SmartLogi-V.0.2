package com.smartlogi.smartlogidms.common.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 *
 * @param <R>  Request DTO type
 * @param <R2> Request DTO type
 * @param <I>          ID type
 */
public interface BaseCrudService<T, R, R2, I> {

    R2 save(R entity);

    R2 update(I id, R requestDTO);

    R2 findById(I id);

    List<R2> findAll();

    Page<R2> findAll(Pageable pageable);

    Page<R2> findAll(Pageable pageable, Specification<T> spec);

    void deleteById(I id);

    boolean existsById(I id);
}