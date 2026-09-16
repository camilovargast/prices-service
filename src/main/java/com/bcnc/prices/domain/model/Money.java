package com.bcnc.prices.domain.model;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

/**
 * Importe monetario.
 *
 * <p>Un precio no es un numero: es un numero con una moneda. Mantenerlos juntos
 * impide comparar o sumar importes de divisas distintas por accidente.
 *
 * <p>El importe es {@link BigDecimal} y nunca {@code double}: la aritmetica en
 * coma flotante binaria no representa exactamente valores decimales como 35,50.
 */
public record Money(BigDecimal amount, Currency currency) {

    public Money {
        Objects.requireNonNull(amount, "el importe es obligatorio");
        Objects.requireNonNull(currency, "la moneda es obligatoria");
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("el importe no puede ser negativo: " + amount);
        }
    }

    public static Money of(BigDecimal amount, String currencyCode) {
        return new Money(amount, Currency.getInstance(currencyCode));
    }
}
