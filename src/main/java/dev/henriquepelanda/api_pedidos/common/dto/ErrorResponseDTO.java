package dev.henriquepelanda.api_pedidos.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@Schema(description = "Standard error response returned by the API.")
public record ErrorResponseDTO(
        @Schema(description = "Error timestamp.", example = "2026-08-15T13:40:00Z")
        Instant timestamp,
        @Schema(description = "Numeric HTTP status code.", example = "400")
        int status,
        @Schema(description = "HTTP status text.", example = "Bad Request")
        String error,
        @Schema(description = "Context message for the error.", example = "Validation failed")
        String message,
        @Schema(description = "Request path that failed.", example = "/clients")
        String path,
        @Schema(description = "Field-level details when validation fails.")
        List<FieldErrorResponseDTO> fieldErrors
) {
}
