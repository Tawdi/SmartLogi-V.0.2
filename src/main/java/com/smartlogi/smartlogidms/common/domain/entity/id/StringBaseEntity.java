package com.smartlogi.smartlogidms.common.domain.entity.id;

import com.smartlogi.smartlogidms.common.domain.entity.SoftDeletableEntity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class StringBaseEntity extends SoftDeletableEntity<String> {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)")
    private String id;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}
