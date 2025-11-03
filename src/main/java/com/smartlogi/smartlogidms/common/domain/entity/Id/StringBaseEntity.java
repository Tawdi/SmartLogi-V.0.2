package com.smartlogi.smartlogidms.common.domain.entity.Id;

import com.smartlogi.smartlogidms.common.domain.entity.BaseEntity;
import com.smartlogi.smartlogidms.common.domain.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class StringBaseEntity extends SoftDeletableEntity<String> {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
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
