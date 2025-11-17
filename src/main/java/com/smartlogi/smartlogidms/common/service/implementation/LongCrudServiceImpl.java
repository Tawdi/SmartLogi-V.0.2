package com.smartlogi.smartlogidms.common.service.implementation;

import com.smartlogi.smartlogidms.common.domain.entity.id.LongBaseEntity;
import com.smartlogi.smartlogidms.common.domain.repository.LongRepository;
import com.smartlogi.smartlogidms.common.mapper.BaseMapper;
import com.smartlogi.smartlogidms.common.service.LongCrudService;

public class LongCrudServiceImpl<T extends LongBaseEntity, R1, R2>
        extends BaseCrudServiceImpl<T, R1, R2, Long>
        implements LongCrudService<T, R1, R2> {

    protected LongCrudServiceImpl(LongRepository<T> repository, BaseMapper<T, R1, R2> mapper) {
        super(repository, mapper);
    }

}
