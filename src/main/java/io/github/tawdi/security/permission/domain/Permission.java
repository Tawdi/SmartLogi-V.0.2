package io.github.tawdi.security.permission.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String code; // Ex: "PACKAGE:READ", "PACKAGE:CREATE", "DRIVER:MANAGE"

    @Column(length = 200)
    private String description;

    @Column(name = "resource_type", nullable = false, length = 50)
    private String resourceType; // "PACKAGE", "DRIVER", "ZONE", "CLIENT"

    @Column(name = "action_type", nullable = false, length = 20)
    private String actionType; // "READ", "CREATE", "UPDATE", "DELETE", "MANAGE"

    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles = new HashSet<>();

    public Permission(String code, String description, String resourceType, String actionType) {
        this.code = code;
        this.description = description;
        this.resourceType = resourceType;
        this.actionType = actionType;
    }
}
