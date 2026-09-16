package com.bcnc.prices.infrastructure.adapter.in.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integracion del endpoint con los datos del enunciado.
 *
 * <p>Recorren la pila completa —HTTP, controlador, caso de uso, JPA y H2— sin
 * dobles de prueba, porque lo que se quiere verificar es precisamente que la
 * tarifa correcta sale de la base de datos y llega al cliente.
 *
 * <p>Los cinco primeros son los escenarios exigidos. Los tres ultimos cubren el
 * comportamiento ante peticiones que no tienen respuesta o no son validas, que
 * el enunciado no pide pero forma parte de un endpoint bien construido.
 */
@SpringBootTest
@AutoConfigureMockMvc
class PriceControllerIT {

    private static final String ENDPOINT = "/api/v1/prices";
    private static final String PRODUCTO_CAMISETA = "35455";
    private static final String CADENA_ZARA = "1";

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Test 1: a las 10:00 del dia 14 aplica la tarifa base (1) a 35,50")
    void test1_peticion_a_las_10_del_dia_14() throws Exception {
        consultarTarifa("2020-06-14T10:00:00")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(1))
                .andExpect(jsonPath("$.price").value(35.50))
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.startDate").value("2020-06-14T00:00:00"))
                .andExpect(jsonPath("$.endDate").value("2020-12-31T23:59:59"));
    }

    @Test
    @DisplayName("Test 2: a las 16:00 del dia 14 la promocion (2) desplaza a la base por prioridad")
    void test2_peticion_a_las_16_del_dia_14() throws Exception {
        consultarTarifa("2020-06-14T16:00:00")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priceList").value(2))
                .andExpect(jsonPath("$.price").value(25.45))
                .andExpect(jsonPath("$.startDate").value("2020-06-14T15:00:00"))
                .andExpect(jsonPath("$.endDate").value("2020-06-14T18:30:00"));
    }

    @Test
    @DisplayName("Test 3: a las 21:00 del dia 14, expirada la promocion, vuelve la tarifa base (1)")
    void test3_peticion_a_las_21_del_dia_14() throws Exception {
        consultarTarifa("2020-06-14T21:00:00")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priceList").value(1))
                .andExpect(jsonPath("$.price").value(35.50));
    }

    @Test
    @DisplayName("Test 4: a las 10:00 del dia 15 aplica la tarifa 3 a 30,50")
    void test4_peticion_a_las_10_del_dia_15() throws Exception {
        consultarTarifa("2020-06-15T10:00:00")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priceList").value(3))
                .andExpect(jsonPath("$.price").value(30.50))
                .andExpect(jsonPath("$.startDate").value("2020-06-15T00:00:00"))
                .andExpect(jsonPath("$.endDate").value("2020-06-15T11:00:00"));
    }

    @Test
    @DisplayName("Test 5: a las 21:00 del dia 16 aplica la tarifa 4 a 38,95")
    void test5_peticion_a_las_21_del_dia_16() throws Exception {
        consultarTarifa("2020-06-16T21:00:00")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priceList").value(4))
                .andExpect(jsonPath("$.price").value(38.95))
                .andExpect(jsonPath("$.startDate").value("2020-06-15T16:00:00"))
                .andExpect(jsonPath("$.endDate").value("2020-12-31T23:59:59"));
    }

    @Test
    @DisplayName("Sin tarifa vigente responde 404 con cuerpo Problem Details")
    void sin_tarifa_vigente_responde_404() throws Exception {
        mockMvc.perform(get(ENDPOINT)
                        .param("productId", PRODUCTO_CAMISETA)
                        .param("brandId", CADENA_ZARA)
                        .param("applicationDate", "2019-01-01T00:00:00"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Tarifa no encontrada"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("Falta un parametro obligatorio: 400")
    void parametro_ausente_responde_400() throws Exception {
        mockMvc.perform(get(ENDPOINT)
                        .param("productId", PRODUCTO_CAMISETA)
                        .param("applicationDate", "2020-06-14T10:00:00"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Fecha con formato invalido: 400")
    void fecha_invalida_responde_400() throws Exception {
        mockMvc.perform(get(ENDPOINT)
                        .param("productId", PRODUCTO_CAMISETA)
                        .param("brandId", CADENA_ZARA)
                        .param("applicationDate", "14-06-2020 10:00"))
                .andExpect(status().isBadRequest());
    }

    private ResultActions consultarTarifa(String applicationDate) throws Exception {
        return mockMvc.perform(get(ENDPOINT)
                .param("productId", PRODUCTO_CAMISETA)
                .param("brandId", CADENA_ZARA)
                .param("applicationDate", applicationDate));
    }
}
