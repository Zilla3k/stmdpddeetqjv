package dev.henriquepelanda.api_pedidos.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record OrderRequestDTO(
        @NotNull
        UUID clientId,
        @NotEmpty
        @Valid
        List<OrderItemRequestDTO> items
) {
}
