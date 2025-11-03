package com.smartlogi.smartlogidms.common.api.controller;

import com.smartlogi.smartlogidms.common.domain.entity.Id.StringBaseEntity;
import com.smartlogi.smartlogidms.common.service.StringCrudService;
import com.smartlogi.smartlogidms.common.mapper.BaseMapper;

public abstract class StringBaseController<T extends StringBaseEntity, RQ, RS> extends AbstractBaseController<T, String, RQ, RS> {

    protected StringBaseController(StringCrudService<RQ, RS> service, BaseMapper<T, RQ, RS> mapper) {
        super(service, mapper);
    }
}