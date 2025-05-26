package com.riesgo.evaluador.dto;

import com.riesgo.evaluador.model.HistorialEvaluacion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialEvaluacionDTO {
    
    private Long id;
    private Long clienteId;
    private String tipoCliente;
    private String nivelRiesgo;
    private BigDecimal puntaje;
    private String observaciones;
    private LocalDateTime fechaEvaluacion;
    private boolean aprobado;
    private BigDecimal montoMaximoCredito;
    private BigDecimal ingresosCliente;
    private BigDecimal totalDeudas;
    
    // Constructor desde entidad
    public static HistorialEvaluacionDTO fromEntity(HistorialEvaluacion historial) {
        return new HistorialEvaluacionDTO(
            historial.getId(),
            historial.getClienteId(),
            historial.getTipoCliente(),
            historial.getNivelRiesgo(),
            historial.getPuntaje(),
            historial.getObservaciones(),
            historial.getFechaEvaluacion(),
            historial.isAprobado(),
            historial.getMontoMaximoCredito(),
            historial.getIngresosCliente(),
            historial.getTotalDeudas()
        );
    }
}