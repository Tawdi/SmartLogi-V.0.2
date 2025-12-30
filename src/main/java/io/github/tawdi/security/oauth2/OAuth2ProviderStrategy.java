package io.github.tawdi.security.oauth2;

import org.springframework.security.oauth2.core.user.OAuth2User;

public interface OAuth2ProviderStrategy {
    String getProviderName();
    String getEmail(OAuth2User oAuth2User);
    String getName(OAuth2User oAuth2User);
    String getProviderId(OAuth2User oAuth2User);
}
