package dev.henriquepelanda.api_pedidos.common.handler;

import dev.henriquepelanda.api_pedidos.common.dto.ErrorResponseDTO;
import dev.henriquepelanda.api_pedidos.common.dto.FieldErrorResponseDTO;
import dev.henriquepelanda.api_pedidos.common.exception.BusinessException;
import dev.henriquepelanda.api_pedidos.common.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ErrorResponseDTO handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return new ErrorResponseDTO(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI(),
                List.of()
        );
    }

    @ExceptionHandler(BusinessException.class)
    public ErrorResponseDTO handleBusiness(BusinessException ex, HttpServletRequest request) {
        return new ErrorResponseDTO(
                Instant.now(),
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI(),
                List.of()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponseDTO handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        var fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new FieldErrorResponseDTO(error.getField(), error.getDefaultMessage()))
                .toList();

        return new ErrorResponseDTO(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation failed",
                request.getRequestURI(),
                fieldErrors
        );
    }
}
