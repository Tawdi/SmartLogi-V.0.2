package com.smartlogi.smartlogidms.common.domain.repository;

import com.smartlogi.smartlogidms.common.domain.entity.id.LongBaseEntity;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface LongRepository<T extends LongBaseEntity>
        extends GenericRepository<T, Long> {
}