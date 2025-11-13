package com.smartlogi.smartlogidms.common.domain.entity.id;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LongBaseEntityTest {


    @Test
    public void ShouldInstance(){

        LongBaseEntity longBaseEntity = new LongBaseEntity();

        longBaseEntity.setId(1L);

        assertThat(longBaseEntity).isInstanceOf(LongBaseEntity.class);
        assertThat(longBaseEntity.getId()).isInstanceOf(Long.class);

    }


}
