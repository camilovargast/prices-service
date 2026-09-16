package com.bcnc.prices.domain.port.in;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Peticion de consulta de tarifa: los tres datos de entrada del enunciado.
 *
 * <p>Agruparlos en un objeto evita el error clasico de intercambiar dos
 * parametros {@code long} consecutivos en la llamada, que el compilador no
 * detectaria.
 */
public record PriceQuery(long productId, long brandId, LocalDateTime applicationDate) {

    public PriceQuery {
        Objects.requireNonNull(applicationDate, "la fecha de aplicacion es obligatoria");
    }
}
