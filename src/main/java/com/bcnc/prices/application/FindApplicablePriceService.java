package com.bcnc.prices.application;

import com.bcnc.prices.domain.exception.PriceNotFoundException;
import com.bcnc.prices.domain.model.Price;
import com.bcnc.prices.domain.port.in.FindApplicablePriceUseCase;
import com.bcnc.prices.domain.port.in.PriceQuery;
import com.bcnc.prices.domain.port.out.PriceRepository;

import java.util.Objects;

/**
 * Caso de uso: obtener la tarifa aplicable.
 *
 * <p>No lleva anotaciones de Spring a proposito. Se declara como bean en
 * {@code infrastructure.config.UseCaseConfiguration}, de modo que esta clase se
 * puede instanciar y probar con un {@code new} y un doble del puerto, sin
 * levantar ningun contexto.
 *
 * <p>Su unica responsabilidad es traducir "no hay resultado" en un fallo del
 * negocio. La seleccion por prioridad no esta aqui: pertenece al adaptador de
 * persistencia, que puede resolverla donde estan los datos.
 */
public class FindApplicablePriceService implements FindApplicablePriceUseCase {

    private final PriceRepository priceRepository;

    public FindApplicablePriceService(PriceRepository priceRepository) {
        this.priceRepository = Objects.requireNonNull(priceRepository, "el repositorio de tarifas es obligatorio");
    }

    @Override
    public Price findApplicablePrice(PriceQuery query) {
        Objects.requireNonNull(query, "la consulta es obligatoria");
        return priceRepository
                .findApplicablePrice(query.productId(), query.brandId(), query.applicationDate())
                .orElseThrow(() -> new PriceNotFoundException(
                        query.productId(), query.brandId(), query.applicationDate()));
    }
}
