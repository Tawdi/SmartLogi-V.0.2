package com.smartlogi.smartlogidms.common.domain.repository;

import com.smartlogi.smartlogidms.common.domain.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GenericRepository<T extends BaseEntity<ID>,ID>
        extends JpaRepository<T, ID> {


}

