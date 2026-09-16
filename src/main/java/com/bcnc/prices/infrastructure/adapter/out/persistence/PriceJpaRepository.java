package com.bcnc.prices.infrastructure.adapter.out.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

interface PriceJpaRepository extends JpaRepository<PriceEntity, Long> {

    /**
     * Devuelve las tarifas vigentes ordenadas por prioridad descendente.
     *
     * <p>La desambiguacion se resuelve en la base de datos: el filtro por rango
     * y la ordenacion viajan al SQL, y el {@link Pageable} se traduce en un
     * limite de una fila. La alternativa —traer todas las tarifas candidatas y
     * quedarse con la de mayor prioridad en memoria— funciona con cuatro filas
     * de ejemplo y se degrada linealmente con el volumen real de la tabla.
     *
     * <p>El desempate secundario por {@code startDate} descendente hace la
     * consulta determinista si dos tarifas empatan en prioridad: gana la que
     * empezo a aplicar mas tarde.
     */
    @Query("""
            SELECT p
            FROM PriceEntity p
            WHERE p.productId = :productId
              AND p.brandId = :brandId
              AND :applicationDate BETWEEN p.startDate AND p.endDate
            ORDER BY p.priority DESC, p.startDate DESC
            """)
    List<PriceEntity> findApplicable(@Param("productId") long productId,
                                     @Param("brandId") long brandId,
                                     @Param("applicationDate") LocalDateTime applicationDate,
                                     Pageable pageable);
}
