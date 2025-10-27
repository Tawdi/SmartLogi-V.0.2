package com.smartlogi.smartlogidms.common.service;

import java.util.List;
import java.util.Optional;

/**
 *
 * @param <T> Entity type
 * @param <ID>  ID type
 */
public interface BaseCrudService<T, ID> {

    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    void deleteById(ID id);
    boolean existsById(ID id);
}