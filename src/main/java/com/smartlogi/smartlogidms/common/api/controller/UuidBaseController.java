package com.smartlogi.smartlogidms.common.api.controller;

import com.smartlogi.smartlogidms.common.domain.UuidBaseEntity;
import com.smartlogi.smartlogidms.common.mapper.BaseMapper;
import com.smartlogi.smartlogidms.common.service.UuidCrudService;

import java.util.UUID;

public abstract class UuidBaseController<T extends UuidBaseEntity, RQ, RS> extends AbstractBaseController<T, UUID, RQ, RS> {

    protected UuidBaseController(UuidCrudService<RQ, RS> service, BaseMapper<T, RQ, RS> mapper) {
        super(service, mapper);
    }
}