package io.github.tawdi.security.oauth2;


import io.github.tawdi.security.jwt.JwtService;
import io.github.tawdi.security.user.domain.UserAccount;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final OAuth2UserService oAuth2UserService;
    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();

        UserAccount user = oAuth2UserService.getOrCreateUser(oAuth2User,registrationId);

        String jwt = jwtService.generateToken(user);

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("""
                {
                    "success": true,
                    "token": "%s",
                    "expiresIn": %d,
                    "user": {
                        "id": %d,
                        "email": "%s",
                        "username": "%s"
                    }
                }
                """.formatted(jwt, jwtService.getExpirationMs(),
                user.getId(), user.getEmail(), user.getUsername()));
    }
}