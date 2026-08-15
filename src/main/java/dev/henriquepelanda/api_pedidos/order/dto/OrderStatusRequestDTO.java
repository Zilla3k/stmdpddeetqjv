package dev.henriquepelanda.api_pedidos.order.dto;

import dev.henriquepelanda.api_pedidos.order.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request to change an order status.")
public record OrderStatusRequestDTO(
        @Schema(description = "New order status.", example = "CONFIRMED")
        @NotNull
        OrderStatus status
) {
}
