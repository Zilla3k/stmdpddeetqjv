package dev.henriquepelanda.api_pedidos.order.dto;

import dev.henriquepelanda.api_pedidos.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponseDTO(
        UUID id,
        UUID clientId,
        OrderStatus status,
        BigDecimal totalAmount,
        Instant createdAt,
        Instant updatedAt,
        List<OrderItemResponseDTO> items
) {
}
