package io.github.tawdi.security.oauth2;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component("facebookProvider")
public class FacebookOAuth2Provider implements OAuth2ProviderStrategy {

    @Override
    public String getProviderName() {
        return "facebook";
    }

    @Override
    public String getEmail(OAuth2User oAuth2User) {

        return oAuth2User.getAttribute("email");
    }

    @Override
    public String getName(OAuth2User oAuth2User) {

        return oAuth2User.getAttribute("name");
    }

    @Override
    public String getProviderId(OAuth2User oAuth2User) {

        return oAuth2User.getAttribute("id");
    }
}