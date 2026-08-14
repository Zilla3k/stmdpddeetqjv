package dev.henriquepelanda.api_pedidos.product.dto;

import java.util.UUID;

public record ProductFilterDTO(
        String name,
        UUID categoryId
) {
}
