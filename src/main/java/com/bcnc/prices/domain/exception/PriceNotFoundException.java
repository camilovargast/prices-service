package com.bcnc.prices.domain.exception;

import java.time.LocalDateTime;

/**
 * No existe tarifa vigente para la combinacion consultada.
 *
 * <p>Es una excepcion de dominio: no sabe nada de HTTP ni de codigos de estado.
 * Traducirla a un 404 es trabajo del adaptador REST.
 */
public class PriceNotFoundException extends RuntimeException {

    private final long productId;
    private final long brandId;
    private final LocalDateTime applicationDate;

    public PriceNotFoundException(long productId, long brandId, LocalDateTime applicationDate) {
        super("No hay tarifa aplicable para el producto %d de la cadena %d en %s"
                .formatted(productId, brandId, applicationDate));
        this.productId = productId;
        this.brandId = brandId;
        this.applicationDate = applicationDate;
    }

    public long getProductId() {
        return productId;
    }

    public long getBrandId() {
        return brandId;
    }

    public LocalDateTime getApplicationDate() {
        return applicationDate;
    }
}
