package com.bcnc.prices.domain.model;

import java.util.Objects;

/**
 * Tarifa aplicable a un producto de una cadena durante un periodo.
 *
 * <p>No tiene identificador: el {@code id} autonumerico de la tabla es un
 * detalle de la persistencia y no significa nada para el negocio. Tampoco
 * modela la prioridad, porque es un desambiguador de la seleccion y no un
 * atributo de la tarifa ya seleccionada: una vez resuelta cual aplica, la
 * prioridad ha dejado de ser relevante.
 */
public record Price(long productId,
                    long brandId,
                    int priceList,
                    DateRange applicationPeriod,
                    Money amount) {

    public Price {
        Objects.requireNonNull(applicationPeriod, "el periodo de aplicacion es obligatorio");
        Objects.requireNonNull(amount, "el importe es obligatorio");
    }
}
