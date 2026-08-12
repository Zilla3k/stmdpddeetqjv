package dev.henriquepelanda.api_pedidos.category.dto;


import jakarta.validation.constraints.NotBlank;

public record CategoryRequestDTO
(
  @NotBlank
  String name,
  @NotBlank
  String description
)
{
}