package com.smartlogi.smartlogidms.common.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

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
@EntityListeners(AuditingEntityListener.class)
public sealed abstract class BaseEntity<ID> permits UuidBaseEntity , StringBaseEntity , LongBaseEntity  {


    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;

    public abstract ID getId();
    public abstract void setId(ID id);
}
