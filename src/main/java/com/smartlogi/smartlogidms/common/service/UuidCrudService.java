package com.smartlogi.smartlogidms.common.service;

import com.smartlogi.smartlogidms.common.domain.entity.id.UuidBaseEntity;

import java.util.UUID;

public interface UuidCrudService<T extends UuidBaseEntity,RequestDTO, ResponseDTO> extends BaseCrudService<T,RequestDTO, ResponseDTO, UUID> {
}
