package com.smartlogi.smartlogidms.common.mapper;

import java.util.List;

/**
 * Generic Mapper interface for converting between Entity, RequestDTO, and ResponseDTO.
 * MapStruct mappers for specific entities will implement this interface.
 *
 * @param <T>      The Entity type
 * @param <ReqDTO> The Request DTO type
 * @param <ResDTO> The Response DTO type
 */
public interface BaseMapper<T, ReqDTO, ResDTO> {

    T requestDtoToEntity(ReqDTO requestDto);

    ResDTO entityToResponseDto(T entity);

    List<ResDTO> entitiesToResponseDtos(List<T> entities);

}