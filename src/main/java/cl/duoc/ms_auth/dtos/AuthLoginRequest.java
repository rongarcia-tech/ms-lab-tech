package cl.duoc.ms_auth.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Un record que representa la solicitud de inicio de sesión.
 * Contiene las credenciales necesarias para la autenticación.
 *
 * @param username El nombre de usuario para la autenticación.
 * @param password La contraseña para la autenticación.
 */
public record AuthLoginRequest(
        @NotBlank @Size(max = 150) String username,
        @NotBlank @Size(min = 8, max = 120) String password
) {}
