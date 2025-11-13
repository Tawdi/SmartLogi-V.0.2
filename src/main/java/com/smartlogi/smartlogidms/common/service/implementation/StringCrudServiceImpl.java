package com.smartlogi.smartlogidms.common.service.implementation;


import com.smartlogi.smartlogidms.common.domain.entity.id.StringBaseEntity;
import com.smartlogi.smartlogidms.common.domain.repository.StringRepository;
import com.smartlogi.smartlogidms.common.mapper.BaseMapper;
import com.smartlogi.smartlogidms.common.service.StringCrudService;

public abstract class StringCrudServiceImpl<T extends StringBaseEntity, RequestDTO, ResponseDTO>
        extends BaseCrudServiceImpl<T, RequestDTO, ResponseDTO, String>
        implements StringCrudService<T,RequestDTO, ResponseDTO> {

    protected StringCrudServiceImpl(StringRepository<T> repository, BaseMapper<T, RequestDTO, ResponseDTO> mapper) {
        super(repository, mapper);
    }

}