package dev.henriquepelanda.api_pedidos.product.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Data for creating a product.")
public record ProductRequestDTO(
  @Schema(description = "Product name.", example = "T-Shirt")
  @NotBlank
  String name,
  @Schema(description = "Product description.", example = "Basic black t-shirt")
  @NotBlank
  String description,
  @Schema(description = "Product price.", example = "79.90")
  @NotNull
  @Positive
  BigDecimal price,
  @Schema(description = "Category linked to the product.", example = "61661601-7f45-499c-bc9c-59918bd68934")
  @NotNull
  UUID categoryId,
  @Schema(description = "Stock quantity.", example = "10")
  @NotNull
  Integer stockQuantity
) {

}
