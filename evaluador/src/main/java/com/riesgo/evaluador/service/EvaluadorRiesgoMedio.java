package com.riesgo.evaluador.service;

import com.riesgo.evaluador.model.Cliente;
import com.riesgo.evaluador.model.PersonaNatural;
import com.riesgo.evaluador.model.PersonaJuridica;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class EvaluadorRiesgoMedio extends EvaluadorRiesgo {

    @Override
    protected BigDecimal aplicarPenalizacionesAdicionales(Cliente cliente, BigDecimal puntajeBase) {
        BigDecimal puntaje = puntajeBase;
        
        // Penalizaciones adicionales para riesgo medio
        if (cliente instanceof PersonaNatural pn) {
            // Penalización por edad de riesgo
            if (pn.getEdad() < 22 || pn.getEdad() > 60) {
                puntaje = puntaje.subtract(new BigDecimal("5"));
            }
            // Penalización por muchos dependientes
            if (pn.getNumeroDependientes() != null && pn.getNumeroDependientes() > 2) {
                puntaje = puntaje.subtract(new BigDecimal("5"));
            }
            // Pequeña bonificación por estabilidad
            if (pn.getEdad() >= 30 && pn.getEdad() <= 50 && 
                (pn.getNumeroDependientes() == null || pn.getNumeroDependientes() <= 2)) {
                puntaje = puntaje.add(new BigDecimal("3"));
            }
        } else if (cliente instanceof PersonaJuridica pj) {
            // Penalización por pocos años de constitución
            if (pj.getAnosConstitucion() < 3) {
                puntaje = puntaje.subtract(new BigDecimal("8"));
            }
            // Pequeña bonificación por experiencia
            if (pj.getAnosConstitucion() >= 7) {
                puntaje = puntaje.add(new BigDecimal("5"));
            }
            // Penalización por sectores volátiles
            if ("CONSTRUCCION".equals(pj.getSectorEconomico()) || 
                "TURISMO".equals(pj.getSectorEconomico())) {
                puntaje = puntaje.subtract(new BigDecimal("3"));
            }
        }
        
        return puntaje;
    }

    @Override
    protected BigDecimal calcularMontoMaximo(Cliente cliente, BigDecimal puntaje) {
        BigDecimal ingresos = cliente.getIngresos();
        BigDecimal multiplicador = new BigDecimal("3"); // 3 veces los ingresos para riesgo medio
        
        // Ajuste por puntaje
        if (puntaje.compareTo(new BigDecimal("75")) >= 0) {
            multiplicador = new BigDecimal("3.5");
        } else if (puntaje.compareTo(new BigDecimal("65")) < 0) {
            multiplicador = new BigDecimal("2.5");
        }
        
        return ingresos.multiply(multiplicador).setScale(2, RoundingMode.HALF_UP);
    }
}