/**
 * Nucleo de la aplicacion: modelo de negocio y puertos.
 *
 * <p>Regla de dependencia: este paquete no importa nada de {@code application},
 * de {@code infrastructure}, de Spring ni de JPA. Si alguna clase de aqui
 * necesita una anotacion de framework, el diseno esta mal.
 *
 * <p>Los puertos se declaran aqui porque los define el dominio segun lo que
 * necesita, no segun lo que la tecnologia ofrece. Sus implementaciones viven
 * en {@code infrastructure}.
 */
package com.bcnc.prices.domain;
