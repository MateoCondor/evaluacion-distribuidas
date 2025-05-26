package com.riesgo.evaluador.service;

import com.riesgo.evaluador.model.Cliente;
import com.riesgo.evaluador.model.PersonaNatural;
import com.riesgo.evaluador.model.PersonaJuridica;
import com.riesgo.evaluador.model.ResultadoEvaluacion;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public abstract class EvaluadorRiesgo {
    
    // Método template que define el flujo de evaluación
    public final ResultadoEvaluacion evaluar(Cliente cliente) {
        BigDecimal puntaje = calcularPuntajeFinal(cliente);
        String nivelRiesgo = determinarNivelRiesgo(puntaje);
        boolean aprobado = determinarAprobacion(puntaje);
        String observaciones = generarObservaciones(cliente, puntaje);
        BigDecimal montoMaximo = calcularMontoMaximo(cliente, puntaje);
        BigDecimal tasaInteres = calcularTasaInteres(nivelRiesgo);
        Integer plazoAprobado = calcularPlazoAprobado(cliente, nivelRiesgo);
        String mensaje = generarMensaje(aprobado, nivelRiesgo);
        
        return ResultadoEvaluacion.crear(
            nivelRiesgo,
            puntaje,
            observaciones,
            aprobado,
            montoMaximo,
            tasaInteres,
            plazoAprobado,
            mensaje
        );
    }
    
    // Método principal para calcular el puntaje final con las nuevas reglas
    private BigDecimal calcularPuntajeFinal(Cliente cliente) {
        BigDecimal puntaje = new BigDecimal("100"); // Puntaje base de 100
        
        // 1. Penalización por puntaje crediticio
        if (cliente.getPuntajeCrediticio() != null && cliente.getPuntajeCrediticio() < 650) {
            puntaje = puntaje.subtract(new BigDecimal("30"));
        }
        
        // 2. Penalización por deudas según tipo de cliente
        if (cliente instanceof PersonaNatural) {
            // Deudas > 40% del ingreso mensual
            if (cliente.getRatioDeudaIngreso().compareTo(new BigDecimal("0.40")) > 0) {
                puntaje = puntaje.subtract(new BigDecimal("15"));
            }
            
            // Monto solicitado > 50% del ingreso mensual
            if (cliente.getMontoSolicitado() != null && 
                cliente.getRatioMontoSolicitadoIngreso().compareTo(new BigDecimal("0.50")) > 0) {
                puntaje = puntaje.subtract(new BigDecimal("10"));
            }
        } else if (cliente instanceof PersonaJuridica) {
            // Para jurídicas, calculamos sobre ingreso anual
            BigDecimal ingresoAnual = cliente.getIngresos().multiply(new BigDecimal("12"));
            
            // Evitar división por cero
            if (ingresoAnual.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal ratioDeudaAnual = cliente.getTotalDeudas()
                    .divide(ingresoAnual, 4, RoundingMode.HALF_UP);
                
                BigDecimal ratioMontoAnual = BigDecimal.ZERO;
                if (cliente.getMontoSolicitado() != null) {
                    ratioMontoAnual = cliente.getMontoSolicitado()
                        .divide(ingresoAnual, 4, RoundingMode.HALF_UP);
                }
                
                // Deudas > 35% del ingreso anual
                if (ratioDeudaAnual.compareTo(new BigDecimal("0.35")) > 0) {
                    puntaje = puntaje.subtract(new BigDecimal("20"));
                }
                
                // Monto solicitado > 30% del ingreso anual
                if (ratioMontoAnual.compareTo(new BigDecimal("0.30")) > 0) {
                    puntaje = puntaje.subtract(new BigDecimal("15"));
                }
            }
        }
        
        // 3. Aplicar penalizaciones adicionales específicas del evaluador
        puntaje = aplicarPenalizacionesAdicionales(cliente, puntaje);
        
        return puntaje.max(BigDecimal.ZERO); // No puede ser negativo
    }
    
    // Método para determinar nivel de riesgo basado en puntaje
    private String determinarNivelRiesgo(BigDecimal puntaje) {
        if (puntaje.compareTo(new BigDecimal("80")) >= 0) {
            return "BAJO";
        } else if (puntaje.compareTo(new BigDecimal("60")) >= 0) {
            return "MEDIO";
        } else {
            return "ALTO";
        }
    }
    
    // Método para determinar aprobación
    private boolean determinarAprobacion(BigDecimal puntaje) {
        return puntaje.compareTo(new BigDecimal("60")) >= 0;
    }
    
    // Calcular tasa de interés según nivel de riesgo
    private BigDecimal calcularTasaInteres(String nivelRiesgo) {
        switch (nivelRiesgo) {
            case "BAJO":
                return new BigDecimal("5.5");
            case "MEDIO":
                return new BigDecimal("8.0");
            case "ALTO":
                return new BigDecimal("12.0");
            default:
                return new BigDecimal("15.0");
        }
    }
    
    // Calcular plazo aprobado
    private Integer calcularPlazoAprobado(Cliente cliente, String nivelRiesgo) {
        Integer plazoSolicitado = cliente.getPlazoEnMeses() != null ? cliente.getPlazoEnMeses() : 12;
        
        switch (nivelRiesgo) {
            case "BAJO":
                return plazoSolicitado; // Se aprueba el plazo solicitado
            case "MEDIO":
                return Math.min(plazoSolicitado, 36); // Máximo 36 meses
            case "ALTO":
                return Math.min(plazoSolicitado, 24); // Máximo 24 meses
            default:
                return 12;
        }
    }
    
    // Generar mensaje según aprobación y nivel
    private String generarMensaje(boolean aprobado, String nivelRiesgo) {
        if (!aprobado) {
            return "Cliente no apto para préstamo";
        }
        
        switch (nivelRiesgo) {
            case "BAJO":
                return "Cliente apto para préstamo con condiciones preferenciales";
            case "MEDIO":
                return "Cliente apto para préstamo con condiciones ajustadas";
            case "ALTO":
                return "Cliente apto para préstamo con condiciones restrictivas";
            default:
                return "Cliente evaluado";
        }
    }
    
    // Métodos abstractos que implementarán las subclases
    protected abstract BigDecimal aplicarPenalizacionesAdicionales(Cliente cliente, BigDecimal puntajeBase);
    protected abstract BigDecimal calcularMontoMaximo(Cliente cliente, BigDecimal puntaje);
    
    // Método común para generar observaciones
    protected String generarObservaciones(Cliente cliente, BigDecimal puntaje) {
        StringBuilder obs = new StringBuilder();
        obs.append("Puntaje final: ").append(puntaje).append(". ");
        
        if (cliente.getPuntajeCrediticio() != null && cliente.getPuntajeCrediticio() < 650) {
            obs.append("Puntaje crediticio bajo. ");
        }
        
        BigDecimal ratioDeuda = cliente.getRatioDeudaIngreso();
        if (ratioDeuda.compareTo(new BigDecimal("0.4")) > 0) {
            obs.append("Alto nivel de endeudamiento. ");
        }
        
        if (cliente.getDeudas() != null && cliente.getDeudas().stream().anyMatch(d -> d.isEnMora())) {
            obs.append("Presenta deudas en mora. ");
        }
        
        return obs.toString();
    }
}