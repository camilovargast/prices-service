package com.bcnc.prices.domain.port.out;

import com.bcnc.prices.domain.model.Price;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Puerto de salida hacia el almacen de tarifas.
 *
 * <p>Devuelve {@link Optional} y no lanza excepcion: para el repositorio, que
 * no exista tarifa es un resultado posible y no un error. Quien decide que eso
 * significa un 404 es el caso de uso, no la persistencia.
 *
 * <p>El contrato obliga a devolver <b>una sola</b> tarifa, ya desambiguada por
 * prioridad. Esa resolucion es responsabilidad del adaptador, que debe
 * hacerla donde estan los datos y no trayendose todas las candidatas a memoria.
 */
public interface PriceRepository {

    Optional<Price> findApplicablePrice(long productId, long brandId, LocalDateTime applicationDate);
}
