package dev.henriquepelanda.api_pedidos.product.dto;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductUpdateDTO(
  String name,
  String description,
  @Positive
  BigDecimal price,
  UUID categoryId,
  Integer stockQuantity
) {
}
