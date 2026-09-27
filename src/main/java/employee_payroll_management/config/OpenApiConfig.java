package employee_payroll_management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI customOpenAPI() {

		return new OpenAPI()
				.info(new Info().title("Employee Payroll Management API").version("1.0.0")
						.description("REST API for Employee and Payroll Management"))
				.components(new Components().addSecuritySchemes("bearerAuth",
						new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
				.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
	}
}