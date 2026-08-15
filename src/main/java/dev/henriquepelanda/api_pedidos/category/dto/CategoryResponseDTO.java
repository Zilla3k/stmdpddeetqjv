package dev.henriquepelanda.api_pedidos.category.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Category representation returned by the API.")
public record CategoryResponseDTO
(
  @Schema(description = "Category identifier.", example = "61661601-7f45-499c-bc9c-59918bd68934")
  UUID id,
  @Schema(description = "Category name.", example = "Electronics")
  String name,
  @Schema(description = "Category description.", example = "General electronic products")
  String description
)
{
}
