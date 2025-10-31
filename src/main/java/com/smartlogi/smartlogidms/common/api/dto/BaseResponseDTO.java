package com.smartlogi.smartlogidms.common.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@ToString
public abstract class BaseResponseDTO<ID>  {

    private ID id;
    private Instant createdAt;
    private Instant updatedAt;
}
