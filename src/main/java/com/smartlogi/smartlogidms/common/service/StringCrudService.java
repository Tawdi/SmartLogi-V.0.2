package com.smartlogi.smartlogidms.common.service;

import com.smartlogi.smartlogidms.common.domain.entity.Id.StringBaseEntity;

public interface StringCrudService<T extends StringBaseEntity,RequestDTO, ResponseDTO> extends BaseCrudService<T,RequestDTO, ResponseDTO, String> {
}
