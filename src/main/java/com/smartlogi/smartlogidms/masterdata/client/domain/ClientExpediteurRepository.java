package com.smartlogi.smartlogidms.masterdata.client.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClientExpediteurRepository extends JpaRepository<ClientExpediteur, UUID> {
}
