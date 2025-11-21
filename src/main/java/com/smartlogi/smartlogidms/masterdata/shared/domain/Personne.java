package com.smartlogi.smartlogidms.masterdata.shared.domain;

import com.smartlogi.smartlogidms.common.domain.entity.id.StringBaseEntity;
import io.github.tawdi.security.user.domain.UserAccount;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@Setter
@Getter
@NoArgsConstructor
public class Personne extends StringBaseEntity {

    @Column(length = 50, name = "first_name", nullable = false)
    private String firstName;

    @Column(length = 50, name = "last_name", nullable = false)
    private String lastName;

    @Column(length = 100, unique = false, nullable = true)
    private String email;

    @Column(length = 20, nullable = false, name = "phone_number")
    private String phoneNumber;

    public Personne(String firstName, String lastName, String email, String phoneNumber) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }


}
