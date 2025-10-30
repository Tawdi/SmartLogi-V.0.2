package com.smartlogi.smartlogidms.common.mapper;

import java.util.List;

import org.mapstruct.*;

/**
 * Generic Mapper interface for converting between Entity, RequestDTO, and ResponseDTO.
 * MapStruct mappers for specific entities will implement this interface.
 *
 * @param <T>      The Entity type
 * @param <ReqDTO> The Request DTO type
 * @param <ResDTO> The Response DTO type
 */
public interface BaseMapper<T, ReqDTO, ResDTO> {

    T toEntity(ReqDTO requestDto);

    ResDTO toDto(T entity);

    List<ResDTO> entitiesToResponseDtos(List<T> entities);

    /**
     * Updates an existing entity with values from DTO
     * MapStruct will automatically implement this to update non-null fields
     */
    void updateEntityFromDto(ReqDTO dto, @MappingTarget T entity);

}