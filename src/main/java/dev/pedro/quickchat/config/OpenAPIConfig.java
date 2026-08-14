package dev.pedro.quickchat.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
public class OpenAPIConfig {

    @Bean
    public OpenAPI defineOpenAPI() {
        Server server = new Server();
        server.setUrl("http://localhost:8027");
        server.setDescription("Development");

        Contact myContact = new Contact();
        myContact.setName("John Doe");
        myContact.setEmail("example@email.com");

        Info information = new Info()
                .title("QuickChat-API")
                .version("1.0.0")
                .description("API of the QuickChat website")
                .contact(myContact);
    
        return new OpenAPI()
                    .info(information)
                    .servers(List.of(server))
                    .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}