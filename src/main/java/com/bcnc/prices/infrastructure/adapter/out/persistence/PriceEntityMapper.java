package com.bcnc.prices.infrastructure.adapter.out.persistence;

import com.bcnc.prices.domain.model.DateRange;
import com.bcnc.prices.domain.model.Money;
import com.bcnc.prices.domain.model.Price;

/**
 * Traduce la fila de base de datos al modelo de negocio.
 *
 * <p>La traduccion es unidireccional porque el servicio solo lee. Este punto
 * es la frontera del hexagono: a partir de aqui hacia dentro no existe JPA.
 */
final class PriceEntityMapper {

    private PriceEntityMapper() {
    }

    static Price toDomain(PriceEntity entity) {
        return new Price(
                entity.getProductId(),
                entity.getBrandId(),
                entity.getPriceList(),
                new DateRange(entity.getStartDate(), entity.getEndDate()),
                Money.of(entity.getPrice(), entity.getCurr()));
    }
}
