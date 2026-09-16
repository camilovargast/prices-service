package com.bcnc.prices.infrastructure.config;

import com.bcnc.prices.application.FindApplicablePriceService;
import com.bcnc.prices.domain.port.in.FindApplicablePriceUseCase;
import com.bcnc.prices.domain.port.out.PriceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cableado de los casos de uso.
 *
 * <p>Declarar los beans aqui, en lugar de anotar el servicio con
 * {@code @Service}, mantiene la capa de aplicacion libre de Spring: el
 * framework es un detalle de infraestructura, incluida la forma de construir
 * los objetos.
 */
@Configuration
public class UseCaseConfiguration {

    @Bean
    public FindApplicablePriceUseCase findApplicablePriceUseCase(PriceRepository priceRepository) {
        return new FindApplicablePriceService(priceRepository);
    }
}
