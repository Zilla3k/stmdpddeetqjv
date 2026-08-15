package dev.henriquepelanda.api_pedidos.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Individual validation error for a request field.")
public record FieldErrorResponseDTO
        (
                @Schema(description = "Invalid field name.", example = "email")
                String field,
                @Schema(description = "Validation message applied to the field.", example = "must not be blank")
                String message
        )
{

}
