package dev.henriquepelanda.api_pedidos.product.dto;

import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Partial data for updating a product.")
public record ProductUpdateDTO(
  @Schema(description = "Product name.", example = "T-Shirt")
  String name,
  @Schema(description = "Product description.", example = "Basic black t-shirt")
  String description,
  @Schema(description = "Product price.", example = "79.90")
  @Positive
  BigDecimal price,
  @Schema(description = "Category linked to the product.", example = "61661601-7f45-499c-bc9c-59918bd68934")
  UUID categoryId,
  @Schema(description = "Stock quantity.", example = "10")
  Integer stockQuantity
) {
}
