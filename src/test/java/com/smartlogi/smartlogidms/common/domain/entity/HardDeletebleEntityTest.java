package com.smartlogi.smartlogidms.common.domain.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HardDeletebleEntityTest {



    @Test
    public void ShouldInstance(){

        HardDeletableEntity<Long> entity = new HardDeletableEntity<>() {

            Long id ;
            @Override
            public Long getId() {
                return id;
            }

            @Override
            public void setId(Long id) {
                this.id =  id ;
            }
        };

        entity.setId(2L);

        assertThat(entity.getId()).isEqualTo(2L);

    }
}
