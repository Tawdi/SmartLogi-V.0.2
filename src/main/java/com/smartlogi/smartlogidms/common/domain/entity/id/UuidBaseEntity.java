package com.smartlogi.smartlogidms.common.domain.entity.id;

import com.smartlogi.smartlogidms.common.domain.entity.SoftDeletableEntity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class UuidBaseEntity extends SoftDeletableEntity<UUID> {

    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(columnDefinition = "UUID DEFAULT gen_random_uuid()", updatable = false, nullable = false)
    private UUID id;

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }
}
