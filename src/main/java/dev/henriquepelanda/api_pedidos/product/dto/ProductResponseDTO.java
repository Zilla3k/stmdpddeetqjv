package dev.henriquepelanda.api_pedidos.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Product representation returned by the API.")
public record ProductResponseDTO(
  @Schema(description = "Product identifier.", example = "116527c3-f6ea-4ae8-a6a5-2baef7bb3761")
  UUID id,
  @Schema(description = "Product name.", example = "T-Shirt")
  String name,
  @Schema(description = "Product description.", example = "Basic black t-shirt")
  String description,
  @Schema(description = "Product price.", example = "79.90")
  BigDecimal price,
  @Schema(description = "Linked category identifier.", example = "61661601-7f45-499c-bc9c-59918bd68934")
  UUID categoryId,
  @Schema(description = "Available stock quantity.", example = "10")
  Integer stockQuantity
) {

}
