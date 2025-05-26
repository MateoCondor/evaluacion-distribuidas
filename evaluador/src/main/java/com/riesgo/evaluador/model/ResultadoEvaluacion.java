package com.riesgo.evaluador.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoEvaluacion {
    
    private String nivelRiesgo; // BAJO, MEDIO, ALTO
    private BigDecimal puntaje;
    private String observaciones;
    private LocalDateTime fechaEvaluacion;
    private boolean aprobado;
    private BigDecimal montoMaximoCredito;
    
    // Nuevos campos para la respuesta mejorada
    private BigDecimal tasaInteres;
    private Integer plazoAprobado;
    private String mensaje;
    
    public static ResultadoEvaluacion crear(String nivelRiesgo, BigDecimal puntaje, 
                                          String observaciones, boolean aprobado, 
                                          BigDecimal montoMaximo, BigDecimal tasaInteres,
                                          Integer plazoAprobado, String mensaje) {
        return new ResultadoEvaluacion(
            nivelRiesgo,
            puntaje,
            observaciones,
            LocalDateTime.now(),
            aprobado,
            montoMaximo,
            tasaInteres,
            plazoAprobado,
            mensaje
        );
    }
}