package dev.henriquepelanda.api_pedidos.category.dto;


import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data for creating a category.")
public record CategoryRequestDTO
(
  @Schema(description = "Category name.", example = "Electronics")
  @NotBlank
  String name,
  @Schema(description = "Category description.", example = "General electronic products")
  @NotBlank
  String description
)
{
}
