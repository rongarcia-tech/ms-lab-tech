package cl.duoc.ms_auth.mappers;

import cl.duoc.ms_auth.dtos.UserCreateRequest;
import cl.duoc.ms_auth.dtos.UserResponse;
import cl.duoc.ms_auth.dtos.UserUpdateRequest;
import cl.duoc.ms_auth.entidades.Role;
import cl.duoc.ms_auth.entidades.User;
import cl.duoc.ms_auth.servicios.UserMapper;

import java.util.Set;
import java.util.UUID;

/**
 * Implementación de la interfaz {@link UserMapper}.
 * Se encarga de convertir entre DTOs (Data Transfer Objects) y entidades de Usuario.
 */
public class UserMapperImpl implements UserMapper {

    /**
     * Convierte un DTO de creación de usuario ({@link UserCreateRequest}) en una nueva entidad {@link User}.
     *
     * @param req El DTO con los datos para el nuevo usuario.
     * @param passwordHash El hash de la contraseña ya codificado.
     * @param rolesResolved El conjunto de entidades {@link Role} que han sido validadas y resueltas.
     * @return Una nueva entidad {@link User} lista para ser persistida.
     */
    @Override
    public User toNewEntity(UserCreateRequest req, String passwordHash, Set<Role> rolesResolved) {
        User u = new User();
        u.setExternalId(UUID.randomUUID());
        u.setUsername(req.username());
        u.setEmail(req.email());
        u.setPasswordHash(passwordHash);
        u.setLabCode(req.labCode());
        u.setActive(boolToYn(req.active()));
        u.setRoles(rolesResolved);
        return u;
    }

    /**
     * Aplica las actualizaciones de un DTO ({@link UserUpdateRequest}) a una entidad {@link User} existente.
     * Solo los campos no nulos en el DTO se actualizan en la entidad.
     *
     * @param entity La entidad {@link User} a actualizar.
     * @param req El DTO con los datos de actualización.
     * @param newPasswordHash El nuevo hash de contraseña, si se proporciona una nueva contraseña.
     * @param rolesResolved El nuevo conjunto de roles resueltos, si se proporciona.
     */
    @Override
    public void applyUpdate(User entity, UserUpdateRequest req, String newPasswordHash, Set<Role> rolesResolved) {
        if (req.email() != null) entity.setEmail(req.email());
        if (req.labCode() != null) entity.setLabCode(req.labCode());
        if (req.active() != null) entity.setActive(boolToYn(req.active()));
        if (req.roles() != null) entity.setRoles(rolesResolved);
        if (req.password() != null) entity.setPasswordHash(newPasswordHash);
    }

    /**
     * Convierte una entidad {@link User} en un DTO de respuesta ({@link UserResponse}).
     * Este DTO se utiliza para enviar la información del usuario de forma segura a los clientes.
     *
     * @param e La entidad {@link User} a convertir.
     * @return Un DTO {@link UserResponse} con los datos públicos del usuario.
     */
    @Override
    public UserResponse toResponse(User e) {
        return new UserResponse(
                e.getId(),
                uuidToString(e.getExternalId()),
                e.getUsername(),
                e.getEmail(),
                rolesToNames(e.getRoles()),
                e.getLabCode(),
                ynToBool(e.getActive()),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
