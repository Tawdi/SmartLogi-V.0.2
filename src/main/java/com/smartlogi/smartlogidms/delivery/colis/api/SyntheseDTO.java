package com.smartlogi.smartlogidms.delivery.colis.api;

public record SyntheseDTO<T>(
        T key,
        long count,
        double poidsTotal
) {}
