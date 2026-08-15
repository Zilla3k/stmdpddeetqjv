package dev.henriquepelanda.api_pedidos.client.dto;

import dev.henriquepelanda.api_pedidos.common.validation.PasswordMatches;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

@PasswordMatches
public record ClientRequestDTO
(
  @NotBlank
  String name,
  @NotBlank
  @Email
  String email,
  @NotBlank
  String document,
  @NotBlank
  @Length(min = 8, max = 16)
  String password,
  @NotBlank
  @Length(min = 8, max = 16)
  String confirmPassword
)
{
}