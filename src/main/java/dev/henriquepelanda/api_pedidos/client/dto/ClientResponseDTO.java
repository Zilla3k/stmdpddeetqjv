package dev.henriquepelanda.api_pedidos.client.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Client representation returned by the API.")
public record ClientResponseDTO
(
  @Schema(description = "Client identifier.", example = "6febd52f-0b06-4f73-b722-ea1db05ddc3b")
  UUID id,
  @Schema(description = "Client name.", example = "Henrique Pelanda")
  String name,
  @Schema(description = "Client email.", example = "henrique@example.com")
  String email,
  @Schema(description = "Client document.", example = "12345678900")
  String document
)
{
}
