package com.smartlogi.smartlogidms.common.service;

import com.smartlogi.smartlogidms.common.domain.entity.id.StringBaseEntity;

public interface StringCrudService<T extends StringBaseEntity,RequestDTO, ResponseDTO> extends BaseCrudService<T,RequestDTO, ResponseDTO, String> {
}
