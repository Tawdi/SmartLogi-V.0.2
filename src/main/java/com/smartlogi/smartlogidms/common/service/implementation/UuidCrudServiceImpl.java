package com.smartlogi.smartlogidms.common.service.implementation;


import com.smartlogi.smartlogidms.common.domain.entity.id.UuidBaseEntity;
import com.smartlogi.smartlogidms.common.domain.repository.UuidRepository;
import com.smartlogi.smartlogidms.common.mapper.BaseMapper;
import com.smartlogi.smartlogidms.common.service.UuidCrudService;

import java.util.UUID;

public class UuidCrudServiceImpl<T extends UuidBaseEntity, RequestDTO, ResponseDTO>
        extends BaseCrudServiceImpl<T, RequestDTO, ResponseDTO, UUID>
        implements UuidCrudService<T,RequestDTO, ResponseDTO> {

    protected UuidCrudServiceImpl(UuidRepository<T> repository, BaseMapper<T, RequestDTO, ResponseDTO> mapper) {
        super(repository, mapper);
    }

}