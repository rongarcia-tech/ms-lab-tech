package cl.duoc.ms_lab.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableConfigurationProperties(JwtAuthProperties.class)
public class SecurityConfig {

    @Bean
    public JwtAuthFilter jwtAuthFilter(JwtAuthProperties props) {
        return new JwtAuthFilter(props); // ya inicializa RemoteJWKSet + cache
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthProperties props) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Swagger / health
                        .requestMatchers("/v3/api-docs/**","/swagger-ui.html","/swagger-ui/**","/actuator/health").permitAll()

                        // LABS: ADMIN para crear/actualizar; lectura autenticada
                        .requestMatchers(HttpMethod.POST, "/labs/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,  "/labs/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,  "/labs/**").hasAnyRole("ADMIN", "LAB_TECH")

                        // ORDERS:
                        .requestMatchers(HttpMethod.POST, "/orders/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/orders/**").hasAnyRole("ADMIN", "LAB_TECH")

                        .anyRequest().authenticated()
                )
                .exceptionHandling(eh -> eh
                        .authenticationEntryPoint(new Json401EntryPoint())
                        .accessDeniedHandler(new Json403AccessDeniedHandler())
                )
                .addFilterBefore(new JwtAuthFilter(props), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
