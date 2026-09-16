package com.bcnc.prices.infrastructure.adapter.in.rest;

import com.bcnc.prices.domain.model.Price;
import com.bcnc.prices.domain.port.in.FindApplicablePriceUseCase;
import com.bcnc.prices.domain.port.in.PriceQuery;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Adaptador de entrada: traduce HTTP a una invocacion del puerto de entrada.
 *
 * <p>No contiene logica: valida la forma de la peticion, construye la consulta,
 * delega y traduce el resultado. Si mañana el mismo caso de uso se expone por
 * mensajeria, esta clase no se toca.
 *
 * <p>Sobre el diseno del endpoint: se consulta un recurso (la tarifa aplicable)
 * mediante GET, con los criterios como parametros de consulta y no en la ruta,
 * porque son filtros y no identifican jerarquicamente al recurso. La ruta lleva
 * version explicita para poder evolucionar el contrato sin romper clientes.
 */
@RestController
@RequestMapping(path = "/api/v1/prices", produces = MediaType.APPLICATION_JSON_VALUE)
public class PriceController {

    private final FindApplicablePriceUseCase findApplicablePriceUseCase;

    PriceController(FindApplicablePriceUseCase findApplicablePriceUseCase) {
        this.findApplicablePriceUseCase = Objects.requireNonNull(findApplicablePriceUseCase);
    }

    /**
     * Devuelve la tarifa vigente para un producto de una cadena en un instante.
     *
     * @param applicationDate instante de aplicacion en formato ISO-8601 local
     *                        (por ejemplo {@code 2020-06-14T10:00:00})
     */
    @GetMapping
    public PriceResponse getApplicablePrice(
            @RequestParam @Positive Long productId,
            @RequestParam @Positive Long brandId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime applicationDate) {

        Price price = findApplicablePriceUseCase.findApplicablePrice(
                new PriceQuery(productId, brandId, applicationDate));

        return PriceResponse.from(price);
    }
}
