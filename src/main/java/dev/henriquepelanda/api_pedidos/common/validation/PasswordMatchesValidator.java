package dev.henriquepelanda.api_pedidos.common.validation;

import dev.henriquepelanda.api_pedidos.client.dto.ClientRequestDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, ClientRequestDTO>
{
  @Override
  public boolean isValid(ClientRequestDTO dto, ConstraintValidatorContext context)
  {
    if (dto == null){
      return true;
    }
    return dto.password() != null && dto.password().equals(dto.confirmPassword());
  };
}
