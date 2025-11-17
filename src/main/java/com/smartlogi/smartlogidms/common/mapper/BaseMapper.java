package com.smartlogi.smartlogidms.common.mapper;

import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * Generic Mapper interface for converting between Entity, RequestDTO, and ResponseDTO.
 * MapStruct mappers for specific entities will implement this interface.
 *
 * @param <T> The Entity type
 * @param <Q> The Request DTO type
 * @param <R> The Response DTO type
 */
public interface BaseMapper<T, Q, R> {

    T toEntity(Q requestDto);

    R toDto(T entity);

    List<R> entitiesToResponseDtos(List<T> entities);

    /**
     * Updates an existing entity with values from DTO
     * MapStruct will automatically implement this to update non-null fields
     */
    void updateEntityFromDto(Q dto, @MappingTarget T entity);

}