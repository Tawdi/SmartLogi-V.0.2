package com.smartlogi.smartlogidms.masterdata.client.domain;

import com.smartlogi.smartlogidms.common.domain.repository.StringRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ClientExpediteurRepository extends StringRepository<ClientExpediteur> {

    Optional<ClientExpediteur> findByEmail(String email);

    @Query("""
            SELECT d FROM ClientExpediteur d
               WHERE LOWER(d.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
               OR LOWER(d.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
               OR d.phoneNumber LIKE CONCAT('%', :searchTerm, '%')
            """)
    Page<ClientExpediteur> searchClients(@Param("searchTerm") String searchTerm, Pageable pageable);
}
