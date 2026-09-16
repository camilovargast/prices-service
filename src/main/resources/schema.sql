DROP TABLE IF EXISTS prices;

CREATE TABLE prices (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    brand_id    BIGINT         NOT NULL,
    start_date  TIMESTAMP      NOT NULL,
    end_date    TIMESTAMP      NOT NULL,
    price_list  INTEGER        NOT NULL,
    product_id  BIGINT         NOT NULL,
    priority    INTEGER        NOT NULL,
    price       DECIMAL(10, 2) NOT NULL,
    curr        VARCHAR(3)     NOT NULL
);

-- Indice de apoyo a la unica consulta del servicio: filtra por producto y
-- cadena y despues acota por rango de fechas. Sin el, la busqueda es un
-- escaneo completo de tabla.
CREATE INDEX idx_prices_lookup ON prices (product_id, brand_id, start_date, end_date);
