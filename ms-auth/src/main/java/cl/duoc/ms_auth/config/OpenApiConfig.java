package cl.duoc.ms_auth.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
/**
 * Clase de configuración para OpenAPI (Swagger).
 * Define la información general de la API, los servidores y la configuración de seguridad.
 */
@Configuration
public class OpenApiConfig {
    /**
     * Configura y crea el bean OpenAPI para la documentación de la API de AgroRent.
     *
     * @return una instancia de {@link OpenAPI} con la configuración personalizada.
     */
    @Bean
    public OpenAPI agroRentOpenAPI() {
        Info info = new Info()
                .title("AgroRent API")
                .description("API para FullStack III.")
                .version("v1.0.0")
                .contact(new Contact()
                        .name("Soporte FullStack III")
                        .email("soporte@fullstack.dev"))
                .license(new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0"));

        List<Server> servers = List.of(
                new Server().url("http://localhost:8080").description("Local"),
                new Server().url("https://api.fullstack.dev").description("Desarrollo")
        );

        Components components = new Components()
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));

        SecurityRequirement security = new SecurityRequirement().addList("bearerAuth");

        return new OpenAPI()
                .info(info)
                .servers(servers)
                .components(components)
                .addSecurityItem(security);
    }
}

