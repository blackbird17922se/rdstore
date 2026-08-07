package com.dsd.rdstore.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorResponseDTO> manejarRecursoNoEncontrado(
                        ResourceNotFoundException exception) {

                ErrorResponseDTO error = new ErrorResponseDTO(
                                HttpStatus.NOT_FOUND.value(),
                                "Recurso no encontrado",
                                exception.getMessage());

                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(error);
        }

        @ExceptionHandler(InvalidCredentialsException.class)
        public ResponseEntity<ErrorResponseDTO> manejarCredencialesInvalidas(
                        InvalidCredentialsException exception) {

                ErrorResponseDTO error = new ErrorResponseDTO(
                                HttpStatus.UNAUTHORIZED.value(),
                                "No autorizado",
                                exception.getMessage());

                return ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .body(error);
        }
}