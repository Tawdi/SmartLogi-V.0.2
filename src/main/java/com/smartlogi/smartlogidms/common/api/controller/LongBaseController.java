package com.smartlogi.smartlogidms.common.api.controller;

import com.smartlogi.smartlogidms.common.domain.entity.id.LongBaseEntity;
import com.smartlogi.smartlogidms.common.service.LongCrudService;
import com.smartlogi.smartlogidms.common.mapper.BaseMapper;

public abstract class LongBaseController<T extends LongBaseEntity, RQ, RS> extends AbstractBaseController<T, Long, RQ, RS> {

    protected LongBaseController(LongCrudService<T,RQ, RS> service, BaseMapper<T, RQ, RS> mapper) {
        super(service, mapper);
    }
}