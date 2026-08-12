package dev.henriquepelanda.api_pedidos.common.dto;

public record FieldErrorResponse
        (
                String field,
                String message
        )
{

}
