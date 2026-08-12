package dev.henriquepelanda.api_pedidos.client.dto;

import java.util.UUID;

public record ClientResponseDTO
(
  UUID id,
  String name,
  String email,
  String document
)
{
}
