package com.riesgo.evaluador.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionResponseDTO {
    
    private Long clienteId;
    private String nombreCliente;
    private String documentoCliente;
    private String tipoCliente;
    private String nivelRiesgo;
    private BigDecimal puntajeFinal; // Cambié de 'puntaje' a 'puntajeFinal'
    private String observaciones;
    private LocalDateTime fechaEvaluacion;
    private boolean aprobado;
    private BigDecimal montoMaximoCredito;
    private BigDecimal ingresosCliente;
    private BigDecimal totalDeudas;
    private String mensaje;
    
    // Nuevos campos para la respuesta mejorada
    private BigDecimal tasaInteres;
    private Integer plazoAprobado;
    private BigDecimal montoSolicitado;
    private Integer plazoSolicitado;
    private Integer puntajeCrediticio;
    
    // Constructor de conveniencia
    public static EvaluacionResponseDTO fromEvaluacion(Long clienteId, String nombre, 
                                                      String documento, String tipoCliente,
                                                      com.riesgo.evaluador.model.ResultadoEvaluacion resultado,
                                                      BigDecimal ingresos, BigDecimal totalDeudas,
                                                      BigDecimal montoSolicitado, Integer plazoSolicitado,
                                                      Integer puntajeCrediticio) {
        EvaluacionResponseDTO response = new EvaluacionResponseDTO();
        response.setClienteId(clienteId);
        response.setNombreCliente(nombre);
        response.setDocumentoCliente(documento);
        response.setTipoCliente(tipoCliente);
        response.setNivelRiesgo(resultado.getNivelRiesgo());
        response.setPuntajeFinal(resultado.getPuntaje());
        response.setObservaciones(resultado.getObservaciones());
        response.setFechaEvaluacion(resultado.getFechaEvaluacion());
        response.setAprobado(resultado.isAprobado());
        response.setMontoMaximoCredito(resultado.getMontoMaximoCredito());
        response.setIngresosCliente(ingresos);
        response.setTotalDeudas(totalDeudas);
        response.setMensaje(resultado.getMensaje());
        response.setTasaInteres(resultado.getTasaInteres());
        response.setPlazoAprobado(resultado.getPlazoAprobado());
        response.setMontoSolicitado(montoSolicitado);
        response.setPlazoSolicitado(plazoSolicitado);
        response.setPuntajeCrediticio(puntajeCrediticio);
        return response;
    }
}