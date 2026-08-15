package dev.henriquepelanda.api_pedidos.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "Data for creating an order.")
public record OrderRequestDTO(
        @Schema(description = "Client identifier.", example = "6febd52f-0b06-4f73-b722-ea1db05ddc3b")
        @NotNull
        UUID clientId,
        @Schema(description = "Order items.")
        @NotEmpty
        @Valid
        List<OrderItemRequestDTO> items
) {
}
