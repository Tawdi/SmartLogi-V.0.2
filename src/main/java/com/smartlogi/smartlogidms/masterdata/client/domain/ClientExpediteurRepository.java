package com.smartlogi.smartlogidms.masterdata.client.domain;

import com.smartlogi.smartlogidms.common.domain.repository.StringRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ClientExpediteurRepository extends StringRepository<ClientExpediteur> {

    Optional<ClientExpediteur> findByEmail(String email);
}
