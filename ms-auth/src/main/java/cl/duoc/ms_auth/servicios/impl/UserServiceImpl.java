package cl.duoc.ms_auth.servicios.impl;

import cl.duoc.ms_auth.dtos.UserCreateRequest;
import cl.duoc.ms_auth.dtos.UserResponse;
import cl.duoc.ms_auth.dtos.UserUpdateRequest;
import cl.duoc.ms_auth.entidades.Role;
import cl.duoc.ms_auth.entidades.User;
import cl.duoc.ms_auth.exceptions.BadRequestException;
import cl.duoc.ms_auth.exceptions.ConflictException;
import cl.duoc.ms_auth.exceptions.NotFoundException;
import cl.duoc.ms_auth.mappers.UserMapperImpl;
import cl.duoc.ms_auth.repositorio.RoleRepository;
import cl.duoc.ms_auth.repositorio.UserRepository;
import cl.duoc.ms_auth.servicios.UserMapper;
import cl.duoc.ms_auth.servicios.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de usuario {@link UserService}.
 * Contiene la lógica de negocio para las operaciones CRUD de usuarios.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper = new UserMapperImpl();

    /**
     * Constructor para inyectar las dependencias necesarias.
     *
     * @param userRepo        El repositorio para acceder a los datos de los usuarios.
     * @param roleRepo        El repositorio para acceder a los datos de los roles.
     * @param passwordEncoder El codificador para hashear las contraseñas.
     */
    public UserServiceImpl(UserRepository userRepo, RoleRepository roleRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Crea un nuevo usuario en el sistema.
     *
     * @param req El DTO con los datos para la creación del usuario.
     * @return Un DTO con la información del usuario recién creado.
     * @throws ConflictException si el nombre de usuario o el email ya existen.
     * @throws BadRequestException si los datos de la solicitud son inválidos (ej. rol no existe, o falta labCode para LAB_TECH).
     */
    @Override
    public UserResponse create(UserCreateRequest req) {
        if (userRepo.existsByUsername(req.username())) throw new ConflictException("USERNAME en uso");
        if (userRepo.existsByEmail(req.email())) throw new ConflictException("EMAIL en uso");

        Set<Role> roles = resolveRoles(req.roles());
        boolean isTech = roles.stream().anyMatch(r -> "LAB_TECH".equalsIgnoreCase(r.getName()));
        if (isTech && (req.labCode() == null || req.labCode().isBlank())) {
            throw new BadRequestException("LAB_CODE es obligatorio para LAB_TECH");
        }

        String hash = passwordEncoder.encode(req.password());
        User entity = mapper.toNewEntity(req, hash, roles);
        entity = userRepo.save(entity);
        return mapper.toResponse(entity);
    }

    /**
     * Actualiza un usuario existente.
     *
     * @param id  El ID del usuario a actualizar.
     * @param req El DTO con los datos a actualizar.
     * @return Un DTO con la información del usuario actualizado.
     * @throws NotFoundException si el usuario no se encuentra.
     * @throws BadRequestException si los datos de la solicitud son inválidos.
     */
    @Override
    public UserResponse update(Long id, UserUpdateRequest req) {
        User u = userRepo.findById(id).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        Set<Role> roles = null;
        if (req.roles() != null) roles = resolveRoles(req.roles());

        String hash = null;
        if (req.password() != null) hash = passwordEncoder.encode(req.password());

        // Validación: si el usuario va a tener el rol LAB_TECH, debe tener un labCode.
        if (roles != null && roles.stream().anyMatch(r -> "LAB_TECH".equalsIgnoreCase(r.getName()))) {
            String finalLabCode = req.labCode() != null ? req.labCode() : u.getLabCode();
            if (finalLabCode == null || finalLabCode.isBlank())
                throw new BadRequestException("LAB_CODE es obligatorio para LAB_TECH");
        }

        mapper.applyUpdate(u, req, hash, roles == null ? u.getRoles() : roles);
        u = userRepo.save(u);
        return mapper.toResponse(u);
    }

    /**
     * Elimina un usuario del sistema (hard delete).
     *
     * @param id El ID del usuario a eliminar.
     * @throws NotFoundException si el usuario no se encuentra.
     */
    @Override
    public void delete(Long id) {
        User u = userRepo.findById(id).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        userRepo.delete(u); // Hard delete (las FK en USER_ROLES se eliminan en cascada).
    }

    /**
     * Obtiene un usuario por su ID.
     *
     * @param id El ID del usuario.
     * @return Un DTO con la información del usuario.
     * @throws NotFoundException si el usuario no se encuentra.
     */
    @Override
    public UserResponse getById(Long id) {
        return userRepo.findById(id).map(mapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
    }

    /**
     * Obtiene una lista de todos los usuarios del sistema.
     *
     * @return Una lista de DTOs con la información de todos los usuarios.
     */
    @Override
    public List<UserResponse> list() {
        return userRepo.findAll().stream().map(mapper::toResponse).toList();
    }

    /**
     * Obtiene la información del usuario actualmente autenticado.
     *
     * @param username El nombre de usuario del principal autenticado.
     * @return Un DTO con la información del usuario.
     * @throws NotFoundException si el usuario no se encuentra.
     */
    @Override
    public UserResponse me(String username) {
        User u = userRepo.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        return mapper.toResponse(u);
    }

    /**
     * Método de utilidad para convertir una lista de nombres de roles en un conjunto de entidades {@link Role}.
     *
     * @param names La lista de nombres de roles.
     * @return Un conjunto de entidades {@link Role}.
     * @throws BadRequestException si un rol no existe o si la lista de roles está vacía.
     */
    private Set<Role> resolveRoles(List<String> names) {
        Set<Role> set = names.stream()
                .map(n -> roleRepo.findByName(n.toUpperCase(Locale.ROOT))
                        .orElseThrow(() -> new BadRequestException("Rol inválido: " + n)))
                .collect(Collectors.toSet());
        if (set.isEmpty()) throw new BadRequestException("Debe indicar al menos un rol");
        return set;
    }
}
