package com.smartlogi.smartlogidms.masterdata.zone.domain;

import com.smartlogi.smartlogidms.common.domain.entity.Id.StringBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Zone extends StringBaseEntity {

    @Column(nullable = false, length = 50)
    private String name;
    @Column(nullable = false, length = 20)
    private String codePostal;

}
