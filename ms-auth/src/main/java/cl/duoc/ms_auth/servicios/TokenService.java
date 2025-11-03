package cl.duoc.ms_auth.servicios;

import cl.duoc.ms_auth.entidades.User;

import java.time.Instant;

public interface TokenService {
    String generateAccessToken(User user);     // incluye roles y labCode
    Instant getExpirationInstant();
}
