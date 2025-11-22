package cl.duoc.ms_auth.dtos;

import java.time.Instant;
import java.util.List;

/**
 * Un record que representa la respuesta de una autenticación exitosa.
 * Contiene el token JWT y la información básica del usuario autenticado.
 *
 * @param token El token de acceso JWT generado.
 * @param expiresAt La fecha y hora de expiración del token.
 * @param userId El ID externo del usuario como una cadena.
 * @param username El nombre de usuario.
 * @param roles La lista de roles asignados al usuario.
 * @param labCode El código del laboratorio asociado al usuario (puede ser nulo, por ejemplo, para administradores).
 */
public record AuthLoginResponse(
        String token,
        Instant expiresAt,
        String userId,         // externalId como string
        String username,
        List<String> roles,
        String labCode         // null si es ADMIN
) {}
