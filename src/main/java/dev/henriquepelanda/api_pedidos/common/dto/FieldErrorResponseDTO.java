package dev.henriquepelanda.api_pedidos.common.dto;

public record FieldErrorResponseDTO
        (
                String field,
                String message
        )
{

}
