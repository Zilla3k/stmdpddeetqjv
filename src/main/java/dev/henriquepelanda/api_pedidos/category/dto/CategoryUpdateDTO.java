package dev.henriquepelanda.api_pedidos.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Partial data for updating a category.")
public record CategoryUpdateDTO(
  @Schema(description = "Category name.", example = "Electronics")
  String name,
  @Schema(description = "Category description.", example = "General electronic products")
  String description
) {
}
