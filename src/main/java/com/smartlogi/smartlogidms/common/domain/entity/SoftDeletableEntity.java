package com.smartlogi.smartlogidms.common.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;
import org.hibernate.annotations.Where;

@MappedSuperclass
//@SQLRestriction("deleted = false")
@SoftDelete
public non-sealed abstract class SoftDeletableEntity<ID> extends BaseEntity<ID> {

//    @Column(nullable = false)
//    @SoftDelete(strategy = SoftDeleteType.ACTIVE)
//    private boolean deleted = false;
//
//    @Override
//    public boolean isDeleted() {
//        return deleted;
//    }
//
//    @Override
//    public void setDeleted(boolean deleted) {
//        this.deleted = deleted;
//    }
}



