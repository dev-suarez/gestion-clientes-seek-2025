package com.desafio.clientes.application.exception;

/**
 * Excepción específica para errores en la gestión de clientes.
 */
public class ClientException extends RuntimeException {

    public ClientException(String message) {
        super(message);
    }

    public ClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
