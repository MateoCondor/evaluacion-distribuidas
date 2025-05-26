package com.riesgo.evaluador.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_evaluaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialEvaluacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;
    
    @Column(name = "tipo_cliente", nullable = false)
    private String tipoCliente;
    
    @Column(name = "nivel_riesgo", nullable = false)
    private String nivelRiesgo;
    
    @Column(nullable = false)
    private BigDecimal puntaje;
    
    @Column(columnDefinition = "TEXT")
    private String observaciones;
    
    @Column(name = "fecha_evaluacion", nullable = false)
    private LocalDateTime fechaEvaluacion;
    
    @Column(nullable = false)
    private boolean aprobado;
    
    @Column(name = "monto_maximo_credito")
    private BigDecimal montoMaximoCredito;
    
    @Column(name = "ingresos_cliente")
    private BigDecimal ingresosCliente;
    
    @Column(name = "total_deudas")
    private BigDecimal totalDeudas;
    
    // Constructor para crear desde ResultadoEvaluacion
    public static HistorialEvaluacion fromResultado(Long clienteId, String tipoCliente, 
                                                   ResultadoEvaluacion resultado,
                                                   BigDecimal ingresos, BigDecimal totalDeudas) {
        HistorialEvaluacion historial = new HistorialEvaluacion();
        historial.setClienteId(clienteId);
        historial.setTipoCliente(tipoCliente);
        historial.setNivelRiesgo(resultado.getNivelRiesgo());
        historial.setPuntaje(resultado.getPuntaje());
        historial.setObservaciones(resultado.getObservaciones());
        historial.setFechaEvaluacion(resultado.getFechaEvaluacion());
        historial.setAprobado(resultado.isAprobado());
        historial.setMontoMaximoCredito(resultado.getMontoMaximoCredito());
        historial.setIngresosCliente(ingresos);
        historial.setTotalDeudas(totalDeudas);
        return historial;
    }
}