package com.bcnc.prices.infrastructure.adapter.out.persistence;

import com.bcnc.prices.domain.model.Price;
import com.bcnc.prices.domain.port.out.PriceRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * Adaptador de salida: implementa el puerto del dominio con Spring Data JPA.
 */
@Repository
public class PriceRepositoryAdapter implements PriceRepository {

    /** Una sola fila: la de mayor prioridad. El limite se aplica en SQL. */
    private static final Pageable SOLO_LA_DE_MAYOR_PRIORIDAD = PageRequest.of(0, 1);

    private final PriceJpaRepository jpaRepository;

    PriceRepositoryAdapter(PriceJpaRepository jpaRepository) {
        this.jpaRepository = Objects.requireNonNull(jpaRepository);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Price> findApplicablePrice(long productId, long brandId, LocalDateTime applicationDate) {
        return jpaRepository
                .findApplicable(productId, brandId, applicationDate, SOLO_LA_DE_MAYOR_PRIORIDAD)
                .stream()
                .findFirst()
                .map(PriceEntityMapper::toDomain);
    }
}
