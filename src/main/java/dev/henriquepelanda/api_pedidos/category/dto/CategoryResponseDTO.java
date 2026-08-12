package dev.henriquepelanda.api_pedidos.category.dto;


import java.util.UUID;

public record CategoryResponseDTO
(
  UUID id,
  String name,
  String description
)
{
}
