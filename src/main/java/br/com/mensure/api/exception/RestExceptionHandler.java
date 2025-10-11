package br.com.mensure.api.exception;

import br.com.mensure.api.dto.ApiErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice // componente global para tratamento de exceções.
public class RestExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class) // Este metodo trata exceções do tipo IllegalArgumentException.
    public ResponseEntity<ApiErrorDTO> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {

        // Monta nosso DTO de erro padronizado.
        ApiErrorDTO errorDetails = new ApiErrorDTO(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(), // 400
                "Bad Request",
                ex.getMessage(), // Pega a mensagem que definimos no service!
                request.getRequestURI());

        // Retorna a resposta com o status 400 e o corpo do erro.
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorDTO> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {

        ApiErrorDTO errorDetails = new ApiErrorDTO(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(), // 404
                "Not Found",
                ex.getMessage(),
                request.getRequestURI());

        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }
}