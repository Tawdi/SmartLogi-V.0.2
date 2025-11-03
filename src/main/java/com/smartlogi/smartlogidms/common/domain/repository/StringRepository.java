package com.smartlogi.smartlogidms.common.domain.repository;

import com.smartlogi.smartlogidms.common.domain.entity.Id.StringBaseEntity;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface StringRepository<T extends StringBaseEntity>
        extends GenericRepository<T, String> {
}