package com.bcnc.prices.domain.port.in;

import com.bcnc.prices.domain.model.Price;

/**
 * Puerto de entrada: unico caso de uso que expone la aplicacion.
 *
 * <p>El adaptador REST depende de esta interfaz, no de su implementacion.
 */
public interface FindApplicablePriceUseCase {

    /**
     * @return la tarifa vigente para el producto y la cadena en el instante dado
     * @throws com.bcnc.prices.domain.exception.PriceNotFoundException si no hay ninguna
     */
    Price findApplicablePrice(PriceQuery query);
}
