package cl.duoc.ms_auth.dtos;

/**
 * Un record que representa la respuesta de un rol.
 * Contiene la información básica de un rol de usuario.
 *
 * @param id El ID del rol.
 * @param name El nombre del rol.
 * @param description La descripción del rol.
 */
public record RoleResponse(
        Long id,
        String name,
        String description
) {}
