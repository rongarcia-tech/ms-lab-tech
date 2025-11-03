package cl.duoc.ms_auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * Filtro de autenticación JWT que se ejecuta una vez por cada solicitud.
 * Intercepta las solicitudes entrantes, extrae y valida el token JWT del encabezado de autorización.
 * Si el token es válido, establece el contexto de seguridad de Spring.
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    /**
     * Constructor que inyecta la utilidad para el manejo de JWT.
     *
     * @param jwtUtils La instancia de {@link JwtUtils} para validar y parsear tokens.
     */
    public JwtAuthFilter(JwtUtils jwtUtils) { this.jwtUtils = jwtUtils; }

    /**
     * Procesa la solicitud para autenticar al usuario a través de un token JWT.
     *
     * @param req La solicitud HTTP.
     * @param res La respuesta HTTP.
     * @param chain La cadena de filtros de servlet.
     * @throws ServletException Si ocurre un error en el servlet.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        // 1. Obtener el encabezado de autorización.
        String auth = req.getHeader(HttpHeaders.AUTHORIZATION);

        // 2. Validar que el encabezado no sea nulo y que comience con "Bearer ".
        if (auth != null && auth.startsWith("Bearer ")) {
            // 3. Extraer el token (sin el prefijo "Bearer ").
            String token = auth.substring(7);

            // 4. Validar y parsear el token usando JwtUtils.
            jwtUtils.validateAndParse(token).ifPresent(payload -> {
                // 5. Si el token es válido, crear la lista de autoridades (roles).
                var authorities = payload.roles().stream()
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r)) // ej: "ADMIN" -> "ROLE_ADMIN"
                        .collect(Collectors.toSet());

                // 6. Crear el objeto de autenticación.
                var authentication = new UsernamePasswordAuthenticationToken(
                        payload.username(), null, authorities
                );

                // 7. Opcionalmente, adjuntar detalles adicionales (el payload completo del token).
                authentication.setDetails(payload);

                // 8. Establecer la autenticación en el contexto de seguridad de Spring.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            });
        }

        // 9. Continuar con la cadena de filtros.
        chain.doFilter(req, res);
    }
}
