package io.github.tawdi.security.oauth2;

import io.github.tawdi.security.jwt.JwtService;
import io.github.tawdi.security.user.domain.UserAccount;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final OAuth2UserService oAuth2UserService;
    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        try {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();

            UserAccount user = oAuth2UserService.getOrCreateUser(oAuth2User, registrationId);
            String jwt = jwtService.generateToken(user);

            // Extract frontend origin from request
            String frontendOrigin = extractFrontendOrigin(request);
            String callbackUrl = frontendOrigin + "/google-callback.html";

            // Build redirect URL with authentication data as query parameters
            String redirectUrl = String.format(
                    "%s?success=true&token=%s&expiresIn=%d&userId=%d&email=%s&username=%s",
                    callbackUrl,
                    URLEncoder.encode(jwt, StandardCharsets.UTF_8),
                    jwtService.getExpirationMs(),
                    user.getId(),
                    URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8),
                    URLEncoder.encode(user.getUsername(), StandardCharsets.UTF_8)
            );

            // Redirect to frontend callback page
            response.sendRedirect(redirectUrl);

        } catch (Exception e) {
            // On error, redirect to callback with error parameter
            String frontendOrigin = extractFrontendOrigin(request);
            String errorUrl = String.format(
                    "%s/google-callback.html?success=false&error=%s",
                    frontendOrigin,
                    URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8)
            );
            response.sendRedirect(errorUrl);
        }
    }

    /**
     * Extract frontend origin from request
     * Checks multiple sources in order of preference
     */
    private String extractFrontendOrigin(HttpServletRequest request) {
        // 1. Check if redirect_uri was passed as query parameter from frontend
        String redirectUri = request.getParameter("redirect_uri");
        if (redirectUri != null && !redirectUri.isEmpty()) {
            return extractOrigin(redirectUri);
        }

        // 2. Check session attribute (set during initial OAuth request)
        HttpSession session = request.getSession(false);
        if (session != null) {
            String savedOrigin = (String) session.getAttribute("oauth2_frontend_origin");
            if (savedOrigin != null) {
                return savedOrigin;
            }
        }

        // 3. Check Referer header
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            return extractOrigin(referer);
        }

        // 4. Fallback to default (development)
        return "http://localhost:5173";
    }

    /**
     * Extract origin (protocol + host + port) from full URL
     */
    private String extractOrigin(String url) {
        try {
            java.net.URI uri = new java.net.URI(url);
            return uri.getScheme() + "://" + uri.getAuthority();
        } catch (Exception e) {
            return "http://localhost:5173"; // fallback
        }
    }
}