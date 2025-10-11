package br.com.mensure.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // Mapeia esta exceção para o status HTTP 404.
public class ResourceNotFoundException extends RuntimeException { // Herda de RuntimeException.

    public ResourceNotFoundException(String message) {
        super(message); // Passa a mensagem para o construtor da classe pai.
    }
}