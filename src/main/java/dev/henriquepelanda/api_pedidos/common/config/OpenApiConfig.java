package dev.henriquepelanda.api_pedidos.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Orders and Inventory API",
                version = "1.0.0",
                description = "API for managing clients, categories, products, and orders with inventory control.",
                contact = @Contact(name = "Henrique Pelanda")
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local")
        }
)
public class OpenApiConfig {
}
