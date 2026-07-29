package com.insightevents.events_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion de la documentacion OpenAPI/Swagger.
 * La UI queda disponible en /swagger-ui.html y el JSON en /v3/api-docs.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI insightEventsOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Plataforma Insight Events - API")
                .description("""
                        API REST para la gestion e investigacion de eventos:
                        administracion de eventos, categorias y analistas,
                        asignaciones (via stored procedure) e historial de auditoria.

                        Nota: las operaciones de escritura aceptan un header opcional
                        'X-Usuario' para registrar en el historial quien realiza la accion.
                        """)
                .version("1.0.0")
                .contact(new Contact().name("Carlos Rodriguez"))
                .license(new License().name("Prueba tecnica")));
    }
}
