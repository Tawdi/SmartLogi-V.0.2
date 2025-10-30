package com.smartlogi.smartlogidms.common.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public abstract class BaseResponseDTO<ID> {

    private ID id;
    private Instant createdAt;
    private Instant updatedAt;
}