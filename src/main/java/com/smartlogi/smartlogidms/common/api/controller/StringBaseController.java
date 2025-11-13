package com.smartlogi.smartlogidms.common.api.controller;

import com.smartlogi.smartlogidms.common.domain.entity.id.StringBaseEntity;
import com.smartlogi.smartlogidms.common.mapper.BaseMapper;
import com.smartlogi.smartlogidms.common.service.StringCrudService;

public abstract class StringBaseController<T extends StringBaseEntity, R1, R2> extends AbstractBaseController<T, String, R1, R2> {

    protected StringBaseController(StringCrudService<T, R1, R2> service, BaseMapper<T, R1, R2> mapper) {
        super(service, mapper);
    }
}