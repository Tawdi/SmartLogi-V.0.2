package com.smartlogi.smartlogidms.common.domain.repository;

import com.smartlogi.smartlogidms.common.domain.UuidBaseEntity;

import java.util.UUID;

public interface UuidRepository<T extends UuidBaseEntity>
        extends GenericRepository<T, UUID> {
}
