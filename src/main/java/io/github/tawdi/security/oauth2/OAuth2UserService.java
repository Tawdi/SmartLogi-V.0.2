package io.github.tawdi.security.oauth2;

import com.smartlogi.smartlogidms.common.exception.ResourceNotFoundException;
import io.github.tawdi.security.permission.repository.RoleRepository;
import io.github.tawdi.security.user.domain.UserAccount;
import io.github.tawdi.security.user.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
//@RequiredArgsConstructor
public class OAuth2UserService {

    private final UserAccountRepository repository;
    private final RoleRepository roleRepository;
    private final Map<String, OAuth2ProviderStrategy> providers = new ConcurrentHashMap<>();

    // Constructor injection of all providers
    public OAuth2UserService(UserAccountRepository repository,
                             RoleRepository roleRepository,
                             Map<String, OAuth2ProviderStrategy> providerStrategies) {
        this.repository = repository;
        this.roleRepository = roleRepository;
        this.providers.putAll(providerStrategies);
    }

    public UserAccount getOrCreateUser(OAuth2User oAuth2User, String registrationId) {
        OAuth2ProviderStrategy provider = getProvider(registrationId);

        String email = provider.getEmail(oAuth2User);
        String name = provider.getName(oAuth2User);
        String providerId = provider.getProviderId(oAuth2User);

        return repository.findByOauth2ProviderAndOauth2Id(provider.getProviderName(), providerId)
                .orElseGet(() -> createOAuthUser(email, name, provider.getProviderName(), providerId));
    }

    private OAuth2ProviderStrategy getProvider(String registrationId) {
        // Map registrationId to provider bean name
        String providerBeanName = registrationId + "Provider";
        OAuth2ProviderStrategy provider = providers.get(providerBeanName);

        if (provider == null) {
            throw new IllegalArgumentException("Unsupported OAuth2 provider: " + registrationId);
        }

        return provider;
    }

    UserAccount createOAuthUser(String email, String name, String provider, String providerId) {
        return repository.save(
                UserAccount.createOAuth2User(email, name, provider, providerId,
                        roleRepository.findByName("CLIENT")
                                .orElseThrow(() -> new ResourceNotFoundException("Role Client not found")))
        );
    }
}