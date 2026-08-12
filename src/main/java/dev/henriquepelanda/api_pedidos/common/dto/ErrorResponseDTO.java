package dev.henriquepelanda.api_pedidos.common.dto;

import java.time.Instant;
import java.util.List;

public record ErrorResponseDTO(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldErrorResponseDTO> fieldErrors
) {
}
