package dev.henriquepelanda.api_pedidos.order.dto;

import dev.henriquepelanda.api_pedidos.order.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(description = "Order representation returned by the API.")
public record OrderResponseDTO(
        @Schema(description = "Order identifier.", example = "5134daf0-d364-4555-becd-e154d2971ffc")
        UUID id,
        @Schema(description = "Client identifier.", example = "6febd52f-0b06-4f73-b722-ea1db05ddc3b")
        UUID clientId,
        @Schema(description = "Current order status.", example = "PENDING")
        OrderStatus status,
        @Schema(description = "Order total amount.", example = "159.80")
        BigDecimal totalAmount,
        @Schema(description = "Order creation timestamp.", example = "2026-08-15T13:40:00Z")
        Instant createdAt,
        @Schema(description = "Order last update timestamp.", example = "2026-08-15T13:40:00Z")
        Instant updatedAt,
        @Schema(description = "Order items.")
        List<OrderItemResponseDTO> items
) {
}
