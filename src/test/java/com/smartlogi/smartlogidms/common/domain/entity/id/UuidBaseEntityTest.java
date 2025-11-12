package com.smartlogi.smartlogidms.common.domain.entity.id;

import com.smartlogi.smartlogidms.common.domain.entity.Id.UuidBaseEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class UuidBaseEntityTest {

    @Test
    public void ShouldInstance(){

        UuidBaseEntity uuidBaseEntity = new UuidBaseEntity();

        uuidBaseEntity.setId(UUID.randomUUID());

        assertThat(uuidBaseEntity).isInstanceOf(UuidBaseEntity.class);
        assertThat(uuidBaseEntity.getId()).isInstanceOf(UUID.class);

    }
}
