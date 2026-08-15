package dev.henriquepelanda.api_pedidos.client.dto;

import jakarta.validation.constraints.Email;

public record ClientUpdateDTO
(
  String name,
  @Email
  String email,
  String document
)
{
}
