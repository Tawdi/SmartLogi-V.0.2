package com.smartlogi.smartlogidms.common.domain.entity;

import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.SoftDelete;

@MappedSuperclass
@SoftDelete
public non-sealed abstract class SoftDeletableEntity<ID> extends BaseEntity<ID> {
}



