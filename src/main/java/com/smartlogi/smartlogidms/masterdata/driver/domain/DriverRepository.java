package com.smartlogi.smartlogidms.masterdata.driver.domain;

import com.smartlogi.smartlogidms.common.domain.repository.StringRepository;
import com.smartlogi.smartlogidms.masterdata.zone.domain.Zone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface DriverRepository extends StringRepository<Driver> {

    Optional<Driver> findByEmail(String email);

    @Query("SELECT d FROM Driver d WHERE d.zoneAssignee.id = :zoneId")
    Page<Driver> findByZoneAssigneeId(@Param("zoneId") String zone, Pageable pageable);

    @Query("""
            SELECT d FROM Driver d
               WHERE LOWER(d.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
               OR LOWER(d.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
               OR d.phoneNumber LIKE CONCAT('%', :searchTerm, '%')
            """)
    Page<Driver> searchDrivers(@Param("searchTerm") String searchTerm, Pageable pageable);

}
