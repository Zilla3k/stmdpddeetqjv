package dev.henriquepelanda.api_pedidos.client.dto;

import dev.henriquepelanda.api_pedidos.common.validation.PasswordMatches;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import io.swagger.v3.oas.annotations.media.Schema;

@PasswordMatches
@Schema(description = "Data for creating a client.")
public record ClientRequestDTO
(
  @Schema(description = "Client name.", example = "Henrique Pelanda")
  @NotBlank
  String name,
  @Schema(description = "Client email.", example = "henrique@example.com")
  @NotBlank
  @Email
  String email,
  @Schema(description = "Client document.", example = "12345678900")
  @NotBlank
  String document,
  @Schema(description = "Access password.", example = "12345678")
  @NotBlank
  @Length(min = 8, max = 16)
  String password,
  @Schema(description = "Access password confirmation.", example = "12345678")
  @NotBlank
  @Length(min = 8, max = 16)
  String confirmPassword
)
{
}
