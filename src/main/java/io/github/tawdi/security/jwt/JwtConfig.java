package io.github.tawdi.security.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "tawdi.jwt")
public class JwtConfig {
    private String secret = "super-secret-key-change-in-prod";
    private long expirationMs = 86400000L; // 24h par défaut
}