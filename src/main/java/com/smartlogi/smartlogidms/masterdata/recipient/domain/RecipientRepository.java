package com.smartlogi.smartlogidms.masterdata.recipient.domain;

import com.smartlogi.smartlogidms.common.domain.repository.StringRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface RecipientRepository extends StringRepository<Recipient> {
    Optional<Recipient> findByEmail(String email);
}
