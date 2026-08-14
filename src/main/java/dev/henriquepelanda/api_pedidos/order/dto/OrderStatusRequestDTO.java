package dev.henriquepelanda.api_pedidos.order.dto;

import dev.henriquepelanda.api_pedidos.order.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record OrderStatusRequestDTO(
        @NotNull
        OrderStatus status
) {
}
