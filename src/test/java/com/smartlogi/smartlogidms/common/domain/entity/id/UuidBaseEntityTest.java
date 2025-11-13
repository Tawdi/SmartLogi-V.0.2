package com.smartlogi.smartlogidms.common.domain.entity.id;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UuidBaseEntityTest {

    @Test
    void ShouldInstance(){

        UuidBaseEntity uuidBaseEntity = new UuidBaseEntity();

        uuidBaseEntity.setId(UUID.randomUUID());

        assertThat(uuidBaseEntity).isInstanceOf(UuidBaseEntity.class);
        assertThat(uuidBaseEntity.getId()).isInstanceOf(UUID.class);

    }
}
