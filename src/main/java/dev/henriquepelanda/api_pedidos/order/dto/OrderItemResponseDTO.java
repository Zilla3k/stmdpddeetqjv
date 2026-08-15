package dev.henriquepelanda.api_pedidos.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Order item returned by the API.")
public record OrderItemResponseDTO(
        @Schema(description = "Product identifier.", example = "116527c3-f6ea-4ae8-a6a5-2baef7bb3761")
        UUID productId,
        @Schema(description = "Product name.", example = "T-Shirt")
        String productName,
        @Schema(description = "Ordered quantity.", example = "2")
        Integer quantity,
        @Schema(description = "Applied unit price.", example = "79.90")
        BigDecimal unitPrice,
        @Schema(description = "Item subtotal.", example = "159.80")
        BigDecimal subtotal
) {
}
