package cl.duoc.ms_lab.security;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth.jwt")
public record JwtAuthProperties(
        String issuer,
        String jwksUri,
        Long allowedSkewSeconds // opcional
) {}
