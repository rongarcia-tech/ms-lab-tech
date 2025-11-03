package cl.duoc.ms_auth.dtos;

import jakarta.validation.constraints.*;
import java.util.List;

/**
 * Un record que representa la solicitud para actualizar un usuario existente.
 * Todos los campos son opcionales; solo los campos proporcionados serán actualizados.
 *
 * @param email La nueva dirección de correo electrónico del usuario.
 * @param password La nueva contraseña para el usuario.
 * @param labCode El nuevo código de laboratorio para el usuario.
 * @param roles La nueva lista de roles para el usuario.
 * @param active El nuevo estado de activación para el usuario.
 */
public record UserUpdateRequest(
        @Email @Size(max = 200) String email,
        @Size(min = 8, max = 120) String password,
        @Pattern(regexp = "^[A-Z0-9_]{3,50}$") String labCode,
        List<@Pattern(regexp = "^[A-Z_]{3,50}$") String> roles,
        Boolean active
) {}
