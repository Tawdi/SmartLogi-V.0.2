package com.smartlogi.smartlogidms.common.domain.entity;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract non-sealed class HardDeletableEntity<I> extends BaseEntity<I> {
}
