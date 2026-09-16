/**
 * Adaptadores: la unica capa que conoce frameworks, protocolos y bases de datos.
 *
 * <p>{@code adapter.in.rest} traduce HTTP a llamadas al puerto de entrada.
 * {@code adapter.out.persistence} implementa el puerto de salida con JPA.
 * Todo lo sustituible del sistema esta aqui dentro.
 */
package com.bcnc.prices.infrastructure;
