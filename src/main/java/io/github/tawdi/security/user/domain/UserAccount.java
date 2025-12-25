package io.github.tawdi.security.user.domain;

import io.github.tawdi.security.permission.domain.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_accounts")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserAccount implements UserDetails {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String email;

    @Column(name = "oauth2_provider")
    private String oauth2Provider; //google

    @Column(name = "oauth2_id")
    private String oauth2Id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    private boolean enabled = true;

    public static UserAccount createOAuth2User(String email, String name,
                                               String provider, String providerId,
                                               Role role) {
        return UserAccount.builder()
                .username(email)
                .email(email)
                .password("smartLogi")
                .role(role)
                .enabled(true)
                .oauth2Provider(provider)
                .oauth2Id(providerId)
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        if (role != null) {
            // Ajouter le rôle
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));

            // Ajouter toutes les permissions du rôle
            role.getPermissions().forEach(permission -> {
                authorities.add(new SimpleGrantedAuthority(permission.getCode()));
            });
        }

        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}