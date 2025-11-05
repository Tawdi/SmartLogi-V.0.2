package com.smartlogi.smartlogidms.common.service.implementation;

import com.smartlogi.smartlogidms.common.domain.entity.Id.LongBaseEntity;
import com.smartlogi.smartlogidms.common.domain.repository.LongRepository;
import com.smartlogi.smartlogidms.common.mapper.BaseMapper;
import com.smartlogi.smartlogidms.common.service.LongCrudService;

public class LongCrudServiceImpl<T extends LongBaseEntity, RequestDTO, ResponseDTO>
        extends BaseCrudServiceImpl<T, RequestDTO, ResponseDTO, Long>
        implements LongCrudService<T,RequestDTO, ResponseDTO> {

    protected LongCrudServiceImpl(LongRepository<T> repository, BaseMapper<T, RequestDTO, ResponseDTO> mapper) {
        super(repository, mapper);
    }

}
