package com.smartlogi.smartlogidms.common.api.controller;

import com.smartlogi.smartlogidms.common.domain.entity.Id.UuidBaseEntity;
import com.smartlogi.smartlogidms.common.mapper.BaseMapper;
import com.smartlogi.smartlogidms.common.service.UuidCrudService;

import java.util.UUID;

public abstract class UuidBaseController<T extends UuidBaseEntity, RQ, RS> extends AbstractBaseController<T, UUID, RQ, RS> {

    protected UuidBaseController(UuidCrudService<T,RQ, RS> service, BaseMapper<T, RQ, RS> mapper) {
        super(service, mapper);
    }
}