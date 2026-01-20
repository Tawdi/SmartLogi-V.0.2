package io.github.tawdi.security.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationResponse {
    private String token;
    private long expiresIn;
    private String role;
    private String type = "Bearer";
}