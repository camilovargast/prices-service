package com.bcnc.prices.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Intervalo temporal en el que una tarifa esta vigente.
 *
 * <p>Ambos extremos son inclusivos: los datos del enunciado terminan en
 * {@code 23:59:59} y empiezan en {@code 00:00:00}, de modo que tratarlos como
 * cerrados es lo que hace que los rangos encajen sin huecos de un segundo.
 */
public record DateRange(LocalDateTime start, LocalDateTime end) {

    public DateRange {
        Objects.requireNonNull(start, "la fecha de inicio es obligatoria");
        Objects.requireNonNull(end, "la fecha de fin es obligatoria");
        if (start.isAfter(end)) {
            throw new IllegalArgumentException(
                    "el inicio del rango (%s) no puede ser posterior a su fin (%s)".formatted(start, end));
        }
    }

    public boolean contains(LocalDateTime instant) {
        Objects.requireNonNull(instant, "el instante a comprobar es obligatorio");
        return !instant.isBefore(start) && !instant.isAfter(end);
    }
}
