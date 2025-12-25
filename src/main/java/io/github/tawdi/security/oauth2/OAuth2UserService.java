package io.github.tawdi.security.oauth2;

import com.smartlogi.smartlogidms.common.exception.ResourceNotFoundException;
import io.github.tawdi.security.permission.repository.RoleRepository;
import io.github.tawdi.security.user.domain.UserAccount;
import io.github.tawdi.security.user.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2UserService {

    private final UserAccountRepository repository;
    private final RoleRepository roleRepository;

    public UserAccount getOrCreateUser(OAuth2User oAuth2User) {

        String name = oAuth2User.getAttribute("name");
        String email = oAuth2User.getAttribute("email");
        String proivderId = oAuth2User.getAttribute("sub");

        return repository.findByOauth2ProviderAndOauth2Id("google", proivderId)

                .orElseGet(() -> createOAuthUser(email, name, "google", proivderId));

    }

    UserAccount createOAuthUser(String email, String name, String provider, String proivderId) {
        return repository.save(
                UserAccount.createOAuth2User(email, name, provider, proivderId,
                        roleRepository.findByName("CLIENT").orElseThrow(() -> new ResourceNotFoundException("Role Cleint not found")))
        );

    }
}