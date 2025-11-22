package cl.duoc.ms_auth.servicios;

import cl.duoc.ms_auth.dtos.UserCreateRequest;
import cl.duoc.ms_auth.dtos.UserResponse;
import cl.duoc.ms_auth.dtos.UserUpdateRequest;

import java.util.List;

public interface UserService {
    UserResponse create(UserCreateRequest request);        // ADMIN
    UserResponse update(Long id, UserUpdateRequest request); // ADMIN
    void delete(Long id);                                   // ADMIN (soft o hard según prefieras)
    UserResponse getById(Long id);                          // ADMIN
    List<UserResponse> list();                              // ADMIN (simple; sin paginar para MVP)
    UserResponse me(String username);                       // cualquier autenticado
}
