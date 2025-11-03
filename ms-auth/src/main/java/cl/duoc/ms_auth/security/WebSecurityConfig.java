package cl.duoc.ms_auth.security;


import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.List;

/**
 * Clase principal de configuración de seguridad web para la aplicación.
 * Habilita la seguridad web y define la cadena de filtros de seguridad, las reglas de autorización,
 * la gestión de sesiones y la configuración de CORS.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    private final KeyProvider keyProvider;
    private final String issuer;

    /**
     * Constructor para inyectar dependencias necesarias para la configuración de JWT.
     *
     * @param keyProvider El proveedor de claves RSA para firmar y verificar tokens.
     * @param issuer El emisor (issuer) esperado en los tokens JWT, inyectado desde las propiedades.
     */
    public WebSecurityConfig(KeyProvider keyProvider, @Value("${auth.jwt.issuer}") String issuer) {
        this.keyProvider = keyProvider;
        this.issuer = issuer;
    }

    /**
     * Configura la cadena de filtros de seguridad principal de Spring Security.
     *
     * @param http El objeto {@link HttpSecurity} para construir la configuración.
     * @return La {@link SecurityFilterChain} construida.
     * @throws Exception si ocurre un error durante la configuración.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Inicializa las utilidades de JWT y el filtro de autenticación
        RSAKey rsaKey = keyProvider.rsaKey();
        var jwtUtils  = new JwtUtils(JwtUtils.toPublicKey(rsaKey), issuer);
        var jwtFilter = new JwtAuthFilter(jwtUtils);

        http
                // Deshabilitar CSRF (Cross-Site Request Forgery) ya que se usan tokens JWT (sin estado).
                .csrf(AbstractHttpConfigurer::disable)
                // Habilitar CORS con la configuración definida en el bean corsConfigurationSource.
                .cors(Customizer.withDefaults())
                // Configurar la gestión de sesiones como STATELESS, ya que la autenticación es por token.
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Definir las reglas de autorización para las solicitudes HTTP.
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos (documentación, login, JWKS, health check).
                        .requestMatchers("/v3/api-docs/**","/swagger-ui.html","/swagger-ui/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/.well-known/jwks.json").permitAll()
                        .requestMatchers(HttpMethod.GET, "/actuator/health").permitAll()

                        // Endpoints protegidos que requieren el rol de ADMIN.
                        .requestMatchers("/users/**").hasRole("ADMIN")
                        .requestMatchers("/roles/**").hasRole("ADMIN")

                        // Endpoint protegido que solo requiere autenticación (cualquier rol).
                        .requestMatchers("/users/me").authenticated()

                        // Cualquier otra solicitud debe ser autenticada.
                        .anyRequest().authenticated()
                )
                // Deshabilitar el formulario de login por defecto.
                .formLogin(AbstractHttpConfigurer::disable)
                // Deshabilitar el logout por defecto.
                .logout(AbstractHttpConfigurer::disable)
                // Agregar el filtro de autenticación JWT antes del filtro estándar de usuario y contraseña.
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configura la fuente de configuración de CORS (Cross-Origin Resource Sharing).
     * Define qué orígenes, métodos y encabezados están permitidos.
     *
     * @return una instancia de {@link CorsConfigurationSource}.
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        var cfg = new CorsConfiguration();
        // Permitir solicitudes desde orígenes locales (localhost y 127.0.0.1) en cualquier puerto.
        cfg.setAllowedOriginPatterns(List.of("http://localhost:*", "http://127.0.0.1:*"));
        // Permitir los métodos HTTP más comunes.
        cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
        // Permitir encabezados comunes necesarios para la autenticación y el tipo de contenido.
        cfg.setAllowedHeaders(List.of("Authorization","Content-Type","Accept"));
        // Permitir el envío de credenciales (como cookies o encabezados de autorización).
        cfg.setAllowCredentials(true);

        var source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        // Aplicar la configuración de CORS a las rutas de la documentación y a todas las demás rutas.
        source.registerCorsConfiguration("/v3/api-docs/**", cfg);
        source.registerCorsConfiguration("/swagger-ui/**", cfg);
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

}
