package cl.duoc.ms_auth.dtos;


import java.time.LocalDateTime;
import java.util.List;

/**
 * Un record que representa la respuesta de un usuario.
 * Contiene la información pública de un usuario.
 *
 * @param id El ID interno del usuario.
 * @param externalId El ID externo del usuario (UUID como cadena).
 * @param username El nombre de usuario.
 * @param email La dirección de correo electrónico del usuario.
 * @param roles La lista de roles asignados al usuario.
 * @param labCode El código del laboratorio asociado al usuario.
 * @param active El estado del usuario (activo o inactivo).
 * @param createdAt La fecha y hora de creación del usuario.
 * @param updatedAt La fecha y hora de la última actualización del usuario.
 */
public record UserResponse(
        Long id,
        String externalId,     // UUID en string
        String username,
        String email,
        List<String> roles,
        String labCode,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
