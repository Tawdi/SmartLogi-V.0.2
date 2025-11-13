package com.smartlogi.smartlogidms.common.api.controller;

import com.smartlogi.smartlogidms.common.domain.entity.id.LongBaseEntity;
import com.smartlogi.smartlogidms.common.service.LongCrudService;
import com.smartlogi.smartlogidms.common.mapper.BaseMapper;

public abstract class LongBaseController<T extends LongBaseEntity, R1, R2> extends AbstractBaseController<T, Long, R1, R2> {

    protected LongBaseController(LongCrudService<T, R1, R2> service, BaseMapper<T, R1, R2> mapper) {
        super(service, mapper);
    }
}