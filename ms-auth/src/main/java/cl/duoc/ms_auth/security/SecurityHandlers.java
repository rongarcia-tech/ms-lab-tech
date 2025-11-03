package cl.duoc.ms_auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.util.Map;

/**
 * Clase de configuración para definir manejadores personalizados de errores de seguridad en Spring Security.
 * Proporciona respuestas JSON estandarizadas para errores de autenticación y autorización.
 */
@Configuration
public class SecurityHandlers {

    /**
     * Define el punto de entrada de autenticación personalizado.
     * Este manejador se activa cuando un usuario no autenticado intenta acceder a un recurso protegido.
     * Devuelve una respuesta HTTP 401 Unauthorized en formato JSON.
     *
     * @param om El {@link ObjectMapper} para serializar la respuesta a JSON.
     * @return una instancia de {@link AuthenticationEntryPoint}.
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(ObjectMapper om) {
        return (request, response, authException) -> writeJson(response, om, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }

    /**
     * Define el manejador de acceso denegado personalizado.
     * Este manejador se activa cuando un usuario autenticado intenta acceder a un recurso para el cual no tiene permisos.
     * Devuelve una respuesta HTTP 403 Forbidden en formato JSON.
     *
     * @param om El {@link ObjectMapper} para serializar la respuesta a JSON.
     * @return una instancia de {@link AccessDeniedHandler}.
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler(ObjectMapper om) {
        return (request, response, accessDeniedException) -> writeJson(response, om, HttpServletResponse.SC_FORBIDDEN, "Forbidden");
    }

    /**
     * Método de utilidad privado para escribir una respuesta de error en formato JSON.
     *
     * @param res La respuesta HTTP del servlet.
     * @param om El ObjectMapper para la conversión a JSON.
     * @param status El código de estado HTTP a establecer.
     * @param message El mensaje de error a incluir en el cuerpo de la respuesta.
     * @throws IOException si ocurre un error al escribir en el stream de respuesta.
     */
    private void writeJson(HttpServletResponse res, ObjectMapper om, int status, String message) throws IOException {
        res.setStatus(status);
        res.setContentType("application/json");
        om.writeValue(res.getWriter(), Map.of("status", status, "error", message));
    }
}
