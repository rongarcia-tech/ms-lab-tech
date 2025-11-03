package cl.duoc.ms_auth.servicios.impl;

import cl.duoc.ms_auth.dtos.AuthLoginRequest;
import cl.duoc.ms_auth.dtos.AuthLoginResponse;
import cl.duoc.ms_auth.entidades.User;
import cl.duoc.ms_auth.exceptions.UnauthorizedException;
import cl.duoc.ms_auth.repositorio.UserRepository;
import cl.duoc.ms_auth.servicios.AuthService;
import cl.duoc.ms_auth.servicios.TokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * Implementación del servicio de autenticación {@link AuthService}.
 * Maneja la lógica de negocio para el inicio de sesión de usuarios.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    /**
     * Constructor para inyectar las dependencias necesarias.
     *
     * @param userRepository  El repositorio para acceder a los datos de los usuarios.
     * @param passwordEncoder El codificador para verificar las contraseñas.
     * @param tokenService    El servicio para generar los tokens de acceso.
     */
    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    /**
     * Procesa una solicitud de inicio de sesión.
     * Verifica las credenciales del usuario, su estado de activación y, si todo es correcto,
     * genera y devuelve un token de acceso junto con la información del usuario.
     *
     * @param request El DTO {@link AuthLoginRequest} que contiene el nombre de usuario y la contraseña.
     * @return Un DTO {@link AuthLoginResponse} con el token y los datos del usuario.
     * @throws UnauthorizedException si las credenciales son inválidas o el usuario está inactivo.
     */
    @Override
    public AuthLoginResponse login(AuthLoginRequest request) {
        // 1. Buscar al usuario por su nombre de usuario.
        User u = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));

        // 2. Verificar si el usuario está activo.
        if (!"Y".equalsIgnoreCase(u.getActive())) {
            throw new UnauthorizedException("Usuario inactivo");
        }

        // 3. Verificar si la contraseña proporcionada coincide con el hash almacenado.
        if (!passwordEncoder.matches(request.password(), u.getPasswordHash())) {
            throw new UnauthorizedException("Credenciales inválidas");
        }

        // 4. Generar el token de acceso para el usuario.
        String token = tokenService.generateAccessToken(u);

        // 5. Recopilar los nombres de los roles del usuario.
        var roles = u.getRoles().stream().map(r -> r.getName()).sorted().collect(Collectors.toList());

        // 6. Construir y devolver la respuesta de inicio de sesión.
        return new AuthLoginResponse(
                token,
                tokenService.getExpirationInstant(),
                u.getExternalId().toString(),
                u.getUsername(),
                roles,
                // Incluir el código de laboratorio solo si el usuario tiene el rol LAB_TECH.
                roles.contains("LAB_TECH") ? u.getLabCode() : null
        );
    }
}
