package dev.henriquepelanda.api_pedidos.client.dto;

import jakarta.validation.constraints.Email;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Partial data for updating a client.")
public record ClientUpdateDTO
(
  @Schema(description = "Client name.", example = "Henrique Pelanda")
  String name,
  @Schema(description = "Client email.", example = "new@email.com")
  @Email
  String email,
  @Schema(description = "Client document.", example = "12345678900")
  String document
)
{
}
