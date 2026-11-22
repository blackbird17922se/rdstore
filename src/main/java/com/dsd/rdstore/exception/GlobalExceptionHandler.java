package com.dsd.rdstore.exception;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

        private static final Pattern CAMPO_DETALLE =
                        Pattern.compile("^detalles\\[(\\d+)]\\..*$");

        /**
         * Convierte errores de @Valid en una respuesta consistente con el resto
         * de los errores del API, incluyendo la posición del producto cuando
         * el error corresponde a un detalle de entrada de inventario.
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponseDTO> manejarValidacion(
                        MethodArgumentNotValidException exception) {

                String mensaje = exception.getBindingResult().getFieldErrors().stream()
                                .map(this::formatearErrorValidacion)
                                .distinct()
                                .collect(Collectors.joining("; "));

                if (mensaje.isBlank()) {
                        mensaje = "Revisa los datos ingresados";
                }

                ErrorResponseDTO error = new ErrorResponseDTO(
                                HttpStatus.BAD_REQUEST.value(),
                                "Datos de entrada inválidos",
                                mensaje);

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(error);
        }

        private String formatearErrorValidacion(FieldError fieldError) {
                String mensaje = fieldError.getDefaultMessage() == null
                                ? "Valor inválido"
                                : fieldError.getDefaultMessage();

                Matcher matcher = CAMPO_DETALLE.matcher(fieldError.getField());
                if (matcher.matches()) {
                        int posicionProducto = Integer.parseInt(matcher.group(1)) + 1;
                        return "Producto " + posicionProducto + ": " + mensaje;
                }
                return mensaje;
        }


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

        @ExceptionHandler(DuplicateResourceException.class)
        public ResponseEntity<ErrorResponseDTO> manejarRecursoDuplicado(
                        DuplicateResourceException exception) {

                ErrorResponseDTO error = new ErrorResponseDTO(
                                HttpStatus.CONFLICT.value(),
                                "Recurso duplicado",
                                exception.getMessage());

                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(error);
        }

        @ExceptionHandler(BusinessRuleException.class)
        public ResponseEntity<ErrorResponseDTO> manejarReglaNegocio(
                        BusinessRuleException exception) {

                ErrorResponseDTO error = new ErrorResponseDTO(
                                400,
                                "Regla de negocio inválida",
                                exception.getMessage());

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(error);
        }
}