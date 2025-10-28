package com.smartlogi.smartlogidms.common.service;

import java.util.List;
import java.util.Optional;

/**
 *
 * @param <RequestDTO> Request DTO type
 * @param <ResponseDTO> Request DTO type
 * @param <ID>     ID type
 */
public interface BaseCrudService<RequestDTO,ResponseDTO, ID> {

    ResponseDTO save(RequestDTO entity);

    Optional<ResponseDTO> findById(ID id);

    List<ResponseDTO> findAll();

    void deleteById(ID id);

    boolean existsById(ID id);
}