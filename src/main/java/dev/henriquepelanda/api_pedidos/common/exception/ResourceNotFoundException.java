package dev.henriquepelanda.api_pedidos.common.exception;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message)
    {
        super(message);
    }
}
