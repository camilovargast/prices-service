# Prices Service

Servicio REST que resuelve, para un producto de una cadena en un instante dado,
la tarifa vigente y su precio final de venta.

Cuando varias tarifas solapan en el tiempo, se aplica la de mayor prioridad.

---

## Ejecucion

Requisitos: **JDK 17** (LTS) y Maven 3.8 o superior.

```bash
mvn spring-boot:run
```

La aplicacion arranca en el puerto 8080 con una base de datos H2 en memoria ya
poblada con los datos del enunciado. Consola de H2 en
`http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:pricesdb`, usuario `sa`,
sin contrasena).

### Ejemplo de consulta

```bash
curl "http://localhost:8080/api/v1/prices?productId=35455&brandId=1&applicationDate=2020-06-14T16:00:00"
```

```json
{
  "productId": 35455,
  "brandId": 1,
  "priceList": 2,
  "startDate": "2020-06-14T15:00:00",
  "endDate": "2020-06-14T18:30:00",
  "price": 25.45,
  "currency": "EUR"
}
```

### Pruebas

```bash
mvn test
```

---

## El endpoint

`GET /api/v1/prices`

| Parametro | Tipo | Descripcion |
|---|---|---|
| `productId` | entero positivo | identificador del producto |
| `brandId` | entero positivo | identificador de la cadena (1 = ZARA) |
| `applicationDate` | ISO-8601 local | instante de aplicacion, `yyyy-MM-ddTHH:mm:ss` |

| Respuesta | Situacion |
|---|---|
| `200` | tarifa encontrada |
| `400` | falta un parametro, o la fecha no tiene formato valido |
| `404` | no hay tarifa vigente para esa combinacion |

Los errores se devuelven en formato **Problem Details (RFC 7807)**.

Los criterios de busqueda viajan como parametros de consulta y no en la ruta
porque filtran un recurso en lugar de identificarlo jerarquicamente. La ruta
lleva version explicita para poder evolucionar el contrato sin romper clientes.

---

## Arquitectura

Arquitectura hexagonal (puertos y adaptadores) en un unico modulo Maven.

```
domain/                     nucleo: no importa Spring, ni JPA, ni Jackson
  model/                      Price, DateRange, Money
  port/in/                    FindApplicablePriceUseCase, PriceQuery
  port/out/                   PriceRepository
  exception/                  PriceNotFoundException

application/                caso de uso: orquesta dominio y puertos
  FindApplicablePriceService

infrastructure/             lo unico que conoce frameworks y protocolos
  adapter/in/rest/            PriceController, PriceResponse, RestExceptionHandler
  adapter/out/persistence/    PriceEntity, PriceJpaRepository, PriceRepositoryAdapter
  config/                     UseCaseConfiguration
```

La regla de dependencia apunta siempre hacia dentro. **No se confia en la
disciplina al nombrar paquetes: se verifica.** `HexagonalArchitectureTest` usa
ArchUnit para comprobar en cada build que el dominio no depende de las capas
externas ni de ningun framework, que la aplicacion no conoce la infraestructura
y que los adaptadores no se hablan entre si.

---

## Decisiones de diseno

**La prioridad se resuelve en la base de datos.** La consulta filtra por rango,
ordena por prioridad descendente y limita a una fila. La alternativa —traer las
tarifas candidatas y quedarse con la mayor en memoria— produce el mismo
resultado con los cuatro registros del ejemplo y se degrada linealmente con el
volumen real de la tabla. El desempate secundario por fecha de inicio hace la
consulta determinista si dos tarifas coincidieran en prioridad.

**El modelo de dominio no tiene identificador.** El `id` autonumerico de la
tabla es un detalle de la persistencia. Tampoco modela la prioridad: es un
desambiguador de la seleccion, no un atributo de la tarifa ya seleccionada.

**El precio es `BigDecimal` y viaja junto a su moneda** en un objeto `Money`.
La coma flotante binaria no representa exactamente valores como 35,50, y un
importe sin divisa invita a sumar euros con dolares.

**El puerto de salida devuelve `Optional` en lugar de lanzar una excepcion.**
Para el repositorio, que no exista tarifa es un resultado posible. Quien decide
que eso significa un 404 es el adaptador REST, a partir de la excepcion de
dominio que lanza el caso de uso. El dominio nunca conoce codigos HTTP.

**El caso de uso no lleva anotaciones de Spring.** Se declara como bean en
`UseCaseConfiguration`, de modo que puede instanciarse con un `new` y probarse
con una lambda como puerto, sin levantar contexto alguno.

**El esquema lo define `schema.sql`, no `ddl-auto`.** El modelo de datos es una
decision explicita y versionada, no un efecto secundario del mapeo de Hibernate.
Incluye un indice por `(product_id, brand_id, start_date, end_date)`: irrelevante
con cuatro filas, pertinente con una tabla real.

**Los extremos del rango son inclusivos.** Los datos del enunciado terminan en
`23:59:59` y empiezan en `00:00:00`, de forma que tratarlos como cerrados evita
huecos de un segundo entre tarifas consecutivas.

**El DTO de respuesta fija el patron de fecha.** El formateo por defecto de
Jackson omite los segundos cuando valen cero, lo que haria variar la forma de la
respuesta segun el dato. Un contrato de API no deberia depender de eso.

---

## Modelo de datos

```sql
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
```

Se anade una clave subrogada `id` porque la tabla del enunciado no declara
ninguna y JPA requiere identidad. No aparece en el modelo de dominio.

---

## Pruebas

**Integracion** (`PriceControllerIT`) — recorren la pila completa (HTTP,
controlador, caso de uso, JPA, H2) sin dobles, porque lo que se verifica es
precisamente que la tarifa correcta sale de la base de datos y llega al cliente.

| Prueba | Peticion | Tarifa | Precio |
|---|---|---|---|
| Test 1 | 14 jun 2020, 10:00 | 1 | 35,50 |
| Test 2 | 14 jun 2020, 16:00 | 2 | 25,45 |
| Test 3 | 14 jun 2020, 21:00 | 1 | 35,50 |
| Test 4 | 15 jun 2020, 10:00 | 3 | 30,50 |
| Test 5 | 16 jun 2020, 21:00 | 4 | 38,95 |

Se anaden tres casos que el enunciado no pide y que forman parte de un endpoint
bien construido: ausencia de tarifa (404), parametro obligatorio ausente (400) y
fecha mal formada (400).

**Unitarias** (`FindApplicablePriceServiceTest`) — el caso de uso con una lambda
como puerto de salida: sin contexto de Spring, sin base de datos y sin libreria
de dobles.

**Arquitectura** (`HexagonalArchitectureTest`) — cuatro reglas de ArchUnit que
verifican la regla de dependencia en cada build.

---

## Posibles evoluciones

No se han implementado por quedar fuera del alcance del enunciado, pero son los
siguientes pasos naturales:

- Documentacion OpenAPI generada a partir del contrato.
- Cache de solo lectura sobre la consulta, que es idempotente y de datos que
  cambian poco.
- Endpoint de altas de tarifas, con validacion de solapamientos por prioridad.
- Sustitucion de H2 por el motor real y migraciones con Flyway o Liquibase; el
  adaptador de persistencia es el unico punto que habria que tocar.
