package com.smartlogi.smartlogidms.common.domain.entity;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public non-sealed abstract class HardDeletableEntity<ID> extends BaseEntity<ID> {
}
