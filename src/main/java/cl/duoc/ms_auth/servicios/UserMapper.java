package cl.duoc.ms_auth.servicios;

import cl.duoc.ms_auth.dtos.UserCreateRequest;
import cl.duoc.ms_auth.dtos.UserResponse;
import cl.duoc.ms_auth.dtos.UserUpdateRequest;
import cl.duoc.ms_auth.entidades.Role;
import cl.duoc.ms_auth.entidades.User;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface UserMapper {
    User toNewEntity(UserCreateRequest req, String passwordHash, Set<Role> rolesResolved);

    void applyUpdate(User entity, UserUpdateRequest req, String newPasswordHash, Set<Role> rolesResolved);

    UserResponse toResponse(User entity);

    // utilidades
    default String uuidToString(UUID id) {
        return id != null ? id.toString() : null;
    }

    default boolean ynToBool(String yn) {
        return "Y".equalsIgnoreCase(yn);
    }

    default String boolToYn(Boolean b) {
        return (b != null && b) ? "Y" : "N";
    }

    default List<String> rolesToNames(Set<Role> roles) {
        return roles.stream().map(Role::getName).sorted().toList();
    }
}
