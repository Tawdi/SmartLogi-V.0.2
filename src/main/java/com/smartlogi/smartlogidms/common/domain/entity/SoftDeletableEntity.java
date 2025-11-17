package com.smartlogi.smartlogidms.common.domain.entity;

import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.SoftDelete;

@MappedSuperclass
@SoftDelete
public abstract non-sealed class SoftDeletableEntity<I> extends BaseEntity<I> {
}



