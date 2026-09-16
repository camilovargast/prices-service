package com.bcnc.prices.infrastructure.adapter.in.rest;

import com.bcnc.prices.domain.exception.PriceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;

/**
 * Traduce excepciones a respuestas HTTP siguiendo RFC 7807 (Problem Details).
 *
 * <p>Es el unico punto del sistema que conoce codigos de estado. El dominio
 * lanza {@link PriceNotFoundException} sin saber que existe el 404.
 *
 * <p>Hereda de {@link ResponseEntityExceptionHandler} para reutilizar el
 * tratamiento estandar de los errores de la propia peticion —parametro
 * ausente, fecha mal formada, validacion incumplida—, que se resuelven como
 * 400 con el mismo formato de cuerpo.
 */
@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(PriceNotFoundException.class)
    ProblemDetail handlePriceNotFound(PriceNotFoundException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        problem.setTitle("Tarifa no encontrada");
        problem.setType(URI.create("urn:bcnc:prices:price-not-found"));
        problem.setProperty("productId", exception.getProductId());
        problem.setProperty("brandId", exception.getBrandId());
        problem.setProperty("applicationDate", exception.getApplicationDate().toString());
        problem.setProperty("timestamp", Instant.now().toString());
        return problem;
    }
}
