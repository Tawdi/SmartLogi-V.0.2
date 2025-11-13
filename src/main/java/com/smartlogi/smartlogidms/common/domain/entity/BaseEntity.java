package com.smartlogi.smartlogidms.common.domain.entity;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

/*
 * A base entity class that provides common fields:
 * - id: is subClasses
 * - createdAt: Timestamp of creation (managed by JPA Auditing)
 * - updatedAt: Timestamp of last update (managed by JPA Auditing)
 */

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public abstract sealed class BaseEntity<I> permits HardDeletableEntity, SoftDeletableEntity {


    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;

    public abstract I getId();
    public abstract void setId(I id);
}
