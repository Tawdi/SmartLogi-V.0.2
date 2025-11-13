package com.smartlogi.smartlogidms.common.api.controller;

import com.smartlogi.smartlogidms.common.domain.entity.id.UuidBaseEntity;
import com.smartlogi.smartlogidms.common.mapper.BaseMapper;
import com.smartlogi.smartlogidms.common.service.UuidCrudService;

import java.util.UUID;

public abstract class UuidBaseController<T extends UuidBaseEntity, R1, R2> extends AbstractBaseController<T, UUID, R1, R2> {

    protected UuidBaseController(UuidCrudService<T, R1, R2> service, BaseMapper<T, R1, R2> mapper) {
        super(service, mapper);
    }
}