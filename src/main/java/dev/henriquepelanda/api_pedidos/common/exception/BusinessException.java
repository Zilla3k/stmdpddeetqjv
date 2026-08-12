package dev.henriquepelanda.api_pedidos.common.exception;

public class BusinessException extends RuntimeException
{
    public BusinessException(String message)
    {
        super(message);
    }
}
