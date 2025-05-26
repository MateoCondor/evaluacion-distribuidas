package com.riesgo.evaluador.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionRequestDTO {
    
    private String nombre;
    private String documento;
    private BigDecimal ingresos;
    private String tipoCliente; // "NATURAL" o "JURIDICA"
    
    // Nuevos campos añadidos
    private BigDecimal montoSolicitado;
    private Integer plazoEnMeses;
    private Integer puntajeCrediticio; // Puntaje inicial del cliente
    
    // Campos para Persona Natural
    private LocalDate fechaNacimiento;
    private String estadoCivil;
    private Integer numeroDependientes;
    private Integer edad; // Nuevo campo opcional (se puede calcular de fechaNacimiento)
    
    // Campos para Persona Jurídica
    private LocalDate fechaConstitucion;
    private String sectorEconomico;
    private Integer numeroEmpleados;
    
    // Lista de deudas actuales
    private List<DeudaDTO> deudas;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeudaDTO {
        
        private String tipo;
        private BigDecimal monto;
        private LocalDate fechaVencimiento;
        private Integer diasMora = 0;
        private Integer plazoMeses; // Nuevo campo para el plazo de la deuda
    }
}