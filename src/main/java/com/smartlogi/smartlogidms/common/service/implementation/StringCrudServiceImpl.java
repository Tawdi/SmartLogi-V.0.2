package com.smartlogi.smartlogidms.common.service.implementation;


import com.smartlogi.smartlogidms.common.domain.entity.id.StringBaseEntity;
import com.smartlogi.smartlogidms.common.domain.repository.StringRepository;
import com.smartlogi.smartlogidms.common.mapper.BaseMapper;
import com.smartlogi.smartlogidms.common.service.StringCrudService;

public abstract class StringCrudServiceImpl<T extends StringBaseEntity, R1, R2>
        extends BaseCrudServiceImpl<T, R1, R2, String>
        implements StringCrudService<T, R1, R2> {

    protected StringCrudServiceImpl(StringRepository<T> repository, BaseMapper<T, R1, R2> mapper) {
        super(repository, mapper);
    }

}