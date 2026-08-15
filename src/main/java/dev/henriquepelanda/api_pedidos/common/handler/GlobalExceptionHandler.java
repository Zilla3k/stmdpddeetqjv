package dev.henriquepelanda.api_pedidos.common.handler;

import dev.henriquepelanda.api_pedidos.common.dto.ErrorResponseDTO;
import dev.henriquepelanda.api_pedidos.common.dto.FieldErrorResponseDTO;
import dev.henriquepelanda.api_pedidos.common.exception.BusinessException;
import dev.henriquepelanda.api_pedidos.common.exception.InvalidRequestException;
import dev.henriquepelanda.api_pedidos.common.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI(), List.of());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseDTO> handleBusiness(BusinessException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request.getRequestURI(), List.of());
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidRequest(InvalidRequestException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI(), List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        var fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toFieldError)
                .toList();

        var globalErrors = ex.getBindingResult().getGlobalErrors().stream()
                .map(this::toFieldError)
                .toList();

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                request.getRequestURI(),
                concat(fieldErrors, globalErrors)
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotReadable(HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Malformed JSON request", request.getRequestURI(), List.of());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO> handleTypeMismatch(HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid request parameter", request.getRequestURI(), List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneric(HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", request.getRequestURI(), List.of());
    }

    private ResponseEntity<ErrorResponseDTO> buildErrorResponse(
            HttpStatus status,
            String message,
            String path,
            List<FieldErrorResponseDTO> fieldErrors
    ) {
        return ResponseEntity.status(status).body(
                new ErrorResponseDTO(
                        Instant.now(),
                        status.value(),
                        status.getReasonPhrase(),
                        message,
                        path,
                        fieldErrors
                )
        );
    }

    private List<FieldErrorResponseDTO> concat(
            List<FieldErrorResponseDTO> fieldErrors,
            List<FieldErrorResponseDTO> globalErrors
    ) {
        return List.copyOf(java.util.stream.Stream.concat(fieldErrors.stream(), globalErrors.stream()).toList());
    }

    private FieldErrorResponseDTO toFieldError(FieldError error) {
        return new FieldErrorResponseDTO(error.getField(), error.getDefaultMessage());
    }

    private FieldErrorResponseDTO toFieldError(ObjectError error) {
        return new FieldErrorResponseDTO(error.getObjectName(), error.getDefaultMessage());
    }
}
