package com.smartlogi.smartlogidms.common.domain.repository;

import com.smartlogi.smartlogidms.common.domain.UuidBaseEntity;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

@NoRepositoryBean
public interface UuidRepository<T extends UuidBaseEntity>
        extends GenericRepository<T, UUID> {
}
