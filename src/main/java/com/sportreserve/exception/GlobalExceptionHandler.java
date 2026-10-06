package com.sportreserve.exception;

import com.sportreserve.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleRecursoNoEncontrado(RecursoNoEncontradoException ex,
                                                                    HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(IdentificacionDuplicadaException.class)
    public ResponseEntity<ErrorResponse> handleIdentificacionDuplicada(IdentificacionDuplicadaException ex,
                                                                       HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(ReservaNoDisponibleException.class)
    public ResponseEntity<ErrorResponse> handleReservaNoDisponible(ReservaNoDisponibleException ex,
                                                                    HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(ReservaInvalidaException.class)
    public ResponseEntity<ErrorResponse> handleReservaInvalida(ReservaInvalidaException ex,
                                                                HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(ParticipanteDuplicadoException.class)
    public ResponseEntity<ErrorResponse> handleParticipanteDuplicado(ParticipanteDuplicadoException ex,
                                                                      HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(ParticipanteNoRegistradoException.class)
    public ResponseEntity<ErrorResponse> handleParticipanteNoRegistrado(ParticipanteNoRegistradoException ex,
                                                                         HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    @ExceptionHandler(PersonaNoAutorizadaException.class)
    public ResponseEntity<ErrorResponse> handlePersonaNoAutorizada(PersonaNoAutorizadaException ex,
                                                                    HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    @ExceptionHandler(CapacidadJugadoresExcedidaException.class)
    public ResponseEntity<ErrorResponse> handleCapacidadJugadoresExcedida(CapacidadJugadoresExcedidaException ex,
                                                                           HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(PagoInvalidoException.class)
    public ResponseEntity<ErrorResponse> handlePagoInvalido(PagoInvalidoException ex,
                                                            HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(PagoNoPermitidoException.class)
    public ResponseEntity<ErrorResponse> handlePagoNoPermitido(PagoNoPermitidoException ex,
                                                               HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(IngresoNoPermitidoException.class)
    public ResponseEntity<ErrorResponse> handleIngresoNoPermitido(IngresoNoPermitidoException ex,
                                                                  HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(MethodArgumentNotValidException ex,
                                                                HttpServletRequest request) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; "));
        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(LocalDateTime.now());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setError("VALIDATION_ERROR");
        error.setMessage(mensaje);
        error.setPath(request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex,
                                                                HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(LocalDateTime.now());
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setError("INTERNAL_SERVER_ERROR");
        error.setMessage("Ocurrio un error interno en el servidor");
        error.setPath(request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message,
                                                         HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(LocalDateTime.now());
        error.setStatus(status.value());
        error.setError(status.name());
        error.setMessage(message);
        error.setPath(request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }

    private String formatFieldError(FieldError fieldError) {
        return "El campo " + fieldError.getField() + ": " + fieldError.getDefaultMessage();
    }
}
