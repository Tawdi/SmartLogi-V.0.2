package io.github.tawdi.security.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @Valid @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @GetMapping("/login/google")
    public void loginGoogle(
            @RequestParam(required = false) String redirect_uri,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        // Save redirect_uri in session for use in success handler
        if (redirect_uri != null && !redirect_uri.isEmpty()) {
            request.getSession().setAttribute("oauth2_frontend_origin", extractOrigin(redirect_uri));
        }

        response.sendRedirect("/oauth2/authorization/google");
    }

    private String extractOrigin(String url) {
        try {
            java.net.URI uri = new java.net.URI(url);
            return uri.getScheme() + "://" + uri.getAuthority();
        } catch (Exception e) {
            return "http://localhost:5173";
        }
    }
}