package dev.henriquepelanda.api_pedidos.product.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponseDTO(
  UUID id,
  String name,
  String description,
  BigDecimal price,
  UUID categoryId,
  Integer stockQuantity
) {

}