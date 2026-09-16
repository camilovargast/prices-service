package com.bcnc.prices.infrastructure.adapter.in.rest;

import com.bcnc.prices.domain.model.Price;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Contrato de salida del endpoint: exactamente los datos que pide el enunciado.
 *
 * <p>Es un DTO propio del adaptador y no el modelo de dominio serializado. Asi
 * el contrato publico de la API no cambia solo porque cambie el modelo interno,
 * y el dominio no necesita anotaciones de Jackson.
 */
public record PriceResponse(long productId,
                            long brandId,
                            int priceList,
                            LocalDateTime startDate,
                            LocalDateTime endDate,
                            BigDecimal price,
                            String currency) {

    static PriceResponse from(Price price) {
        return new PriceResponse(
                price.productId(),
                price.brandId(),
                price.priceList(),
                price.applicationPeriod().start(),
                price.applicationPeriod().end(),
                price.amount().amount(),
                price.amount().currency().getCurrencyCode());
    }
}
