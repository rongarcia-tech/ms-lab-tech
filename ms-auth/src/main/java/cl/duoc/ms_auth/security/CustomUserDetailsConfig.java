package cl.duoc.ms_auth.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Clase de configuración para la gestión de detalles de usuario en Spring Security.
 * Define cómo se codifican las contraseñas y cómo se cargan los detalles de los usuarios.
 */
@Configuration
public class CustomUserDetailsConfig {

    /**
     * Define el bean para el codificador de contraseñas.
     * Se utiliza BCryptPasswordEncoder, que es un estándar fuerte y seguro para el hashing de contraseñas.
     *
     * @return una instancia de {@link PasswordEncoder}.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Define el bean para el servicio de detalles de usuario.
     * En esta configuración, se utiliza un {@link InMemoryUserDetailsManager} para gestionar
     * usuarios en memoria, lo cual es útil para pruebas o aplicaciones simples.
     *
     * @param encoder El codificador de contraseñas para codificar las contraseñas de los usuarios en memoria.
     * @return una instancia de {@link UserDetailsService} con usuarios predefinidos.
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {

        // Creación de un usuario de ejemplo con el rol "USER".
        UserDetails user1 = User.withUsername("user1")
                .passwordEncoder(encoder::encode)
                .password("password")
                .roles("USER")          // ROLE_USER
                .build();

        // Creación de un usuario de ejemplo con el rol "ADMIN".
        UserDetails user2 = User.withUsername("user2")
                .passwordEncoder(encoder::encode)
                .password("password")
                .roles("ADMIN")         // ROLE_ADMIN
                .build();

        // Creación de un usuario de ejemplo con ambos roles, "USER" y "ADMIN".
        UserDetails user3 = User.withUsername("user3")
                .passwordEncoder(encoder::encode)
                .password("password")
                .roles("USER", "ADMIN") // ambos roles
                .build();

        // Devuelve un gestor de detalles de usuario en memoria con los usuarios creados.
        return new InMemoryUserDetailsManager(user1, user2, user3);
    }
}
