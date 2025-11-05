package com.smartlogi.smartlogidms.common.service;

import com.smartlogi.smartlogidms.common.domain.entity.Id.LongBaseEntity;

public interface LongCrudService<T extends LongBaseEntity,RequestDTO, ResponseDTO> extends BaseCrudService<T,RequestDTO, ResponseDTO, Long> {
}
