package com.smartlogi.smartlogidms.common.service;

import com.smartlogi.smartlogidms.common.domain.entity.id.UuidBaseEntity;

import java.util.UUID;

public interface UuidCrudService<T extends UuidBaseEntity, R1, R2> extends BaseCrudService<T, R1, R2, UUID> {
}
