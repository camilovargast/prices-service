package com.bcnc.prices.infrastructure.adapter.in.rest;

import com.bcnc.prices.domain.model.Price;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Contrato de salida del endpoint: exactamente los datos que pide el enunciado.
 *
 * <p>Es un DTO propio del adaptador y no el modelo de dominio serializado. Asi
 * el contrato publico de la API no cambia solo porque cambie el modelo interno,
 * y el dominio no necesita anotaciones de Jackson.
 *
 * <p>El patron de fecha se fija de forma explicita porque el formateo por
 * defecto de Jackson omite los segundos cuando valen cero
 * ({@code 2020-06-14T00:00}), lo que haria variar la forma de la respuesta
 * segun el dato. Un contrato de API no deberia depender de eso.
 */
public record PriceResponse(long productId,
                            long brandId,
                            int priceList,

                            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
                            LocalDateTime startDate,

                            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
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
