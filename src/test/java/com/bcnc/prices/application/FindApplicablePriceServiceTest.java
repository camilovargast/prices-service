package com.bcnc.prices.application;

import com.bcnc.prices.domain.exception.PriceNotFoundException;
import com.bcnc.prices.domain.model.DateRange;
import com.bcnc.prices.domain.model.Money;
import com.bcnc.prices.domain.model.Price;
import com.bcnc.prices.domain.port.in.PriceQuery;
import com.bcnc.prices.domain.port.out.PriceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Prueba unitaria del caso de uso.
 *
 * <p>Sin contexto de Spring, sin base de datos y sin libreria de dobles: el
 * puerto de salida tiene un solo metodo, asi que una lambda basta como
 * implementacion de prueba. Que esto sea posible es la ventaja practica de la
 * arquitectura hexagonal, y no un detalle de estilo: estos tests corren en
 * milisegundos frente a los segundos que cuesta levantar el contexto.
 */
class FindApplicablePriceServiceTest {

    private static final LocalDateTime INSTANTE = LocalDateTime.of(2020, 6, 14, 10, 0);
    private static final PriceQuery CONSULTA = new PriceQuery(35455L, 1L, INSTANTE);

    private static final Price TARIFA = new Price(
            35455L, 1L, 1,
            new DateRange(LocalDateTime.of(2020, 6, 14, 0, 0), LocalDateTime.of(2020, 12, 31, 23, 59, 59)),
            Money.of(new BigDecimal("35.50"), "EUR"));

    @Test
    @DisplayName("Devuelve la tarifa que entrega el puerto de salida")
    void devuelve_la_tarifa_del_puerto() {
        PriceRepository repositorio = (productId, brandId, fecha) -> Optional.of(TARIFA);

        Price resultado = new FindApplicablePriceService(repositorio).findApplicablePrice(CONSULTA);

        assertSame(TARIFA, resultado);
    }

    @Test
    @DisplayName("Traduce la ausencia de tarifa en un fallo de negocio")
    void traduce_la_ausencia_en_excepcion() {
        PriceRepository repositorio = (productId, brandId, fecha) -> Optional.empty();
        FindApplicablePriceService servicio = new FindApplicablePriceService(repositorio);

        PriceNotFoundException error = assertThrows(PriceNotFoundException.class,
                () -> servicio.findApplicablePrice(CONSULTA));

        assertEquals(35455L, error.getProductId());
        assertEquals(1L, error.getBrandId());
        assertEquals(INSTANTE, error.getApplicationDate());
    }

    @Test
    @DisplayName("Traslada al puerto los tres criterios de la consulta sin alterarlos")
    void traslada_los_criterios_al_puerto() {
        PriceRepository repositorio = (productId, brandId, fecha) -> {
            assertEquals(35455L, productId);
            assertEquals(1L, brandId);
            assertEquals(INSTANTE, fecha);
            return Optional.of(TARIFA);
        };

        new FindApplicablePriceService(repositorio).findApplicablePrice(CONSULTA);
    }
}
