package dev.henriquepelanda.api_pedidos.product.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductRequestDTO(
  @NotBlank
  String name,
  @NotBlank
  String description,
  @NotNull
  @Positive
  BigDecimal price,
  @NotNull
  UUID categoryId,
  @NotNull
  Integer stockQuantity
) {

}
