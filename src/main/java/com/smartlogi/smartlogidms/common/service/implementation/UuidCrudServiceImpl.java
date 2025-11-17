package com.smartlogi.smartlogidms.common.service.implementation;


import com.smartlogi.smartlogidms.common.domain.entity.id.UuidBaseEntity;
import com.smartlogi.smartlogidms.common.domain.repository.UuidRepository;
import com.smartlogi.smartlogidms.common.mapper.BaseMapper;
import com.smartlogi.smartlogidms.common.service.UuidCrudService;

import java.util.UUID;

public class UuidCrudServiceImpl<T extends UuidBaseEntity, R1, R2>
        extends BaseCrudServiceImpl<T, R1, R2, UUID>
        implements UuidCrudService<T, R1, R2> {

    protected UuidCrudServiceImpl(UuidRepository<T> repository, BaseMapper<T, R1, R2> mapper) {
        super(repository, mapper);
    }

}