package com.smartlogi.smartlogidms.common.domain.repository;

import com.smartlogi.smartlogidms.common.domain.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;


@NoRepositoryBean
public interface GenericRepository<T extends BaseEntity<ID>,ID>
        extends JpaRepository<T, ID> {


}

