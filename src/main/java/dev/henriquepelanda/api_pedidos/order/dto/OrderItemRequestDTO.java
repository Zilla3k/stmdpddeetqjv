package dev.henriquepelanda.api_pedidos.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Item sent to compose an order.")
public record OrderItemRequestDTO(
        @Schema(description = "Product identifier.", example = "116527c3-f6ea-4ae8-a6a5-2baef7bb3761")
        @NotNull
        UUID productId,
        @Schema(description = "Requested quantity.", example = "2")
        @NotNull
        @Positive
        Integer quantity
) {
}
