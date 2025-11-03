package cl.duoc.ms_auth.dtos;

import jakarta.validation.constraints.*;
import java.util.List;

/**
 * Un record que representa la solicitud para crear un nuevo usuario.
 * Contiene todos los datos necesarios para el registro de un usuario.
 *
 * @param username El nombre de usuario. Debe ser único.
 * @param email La dirección de correo electrónico del usuario. Debe ser única.
 * @param password La contraseña para el nuevo usuario.
 * @param labCode El código del laboratorio asociado al usuario. Debe estar en mayúsculas, números o guion bajo, con una longitud de 3 a 50 caracteres.
 * @param roles La lista de nombres de roles a asignar al usuario. Los roles deben existir en la base de datos.
 * @param active El estado del usuario (activo o inactivo).
 */
public record UserCreateRequest(
        @NotBlank @Size(max = 150) String username,
        @NotBlank @Email @Size(max = 200) String email,
        @NotBlank @Size(min = 8, max = 120) String password,
        // Solo letras mayúsculas, números o guion bajo; 3..50
        @Pattern(regexp = "^[A-Z0-9_]{3,50}$", message = "LAB_CODE debe ser MAYÚSCULAS/NÚMEROS/_ (3-50)")
        String labCode,
        // roles permitidos, validar en servicio que existan en BD
        @NotEmpty List<@Pattern(regexp = "^[A-Z_]{3,50}$") String> roles,
        @NotNull Boolean active
) {}
