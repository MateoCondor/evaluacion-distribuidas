package com.riesgo.evaluador.service;

import com.riesgo.evaluador.model.Cliente;
import com.riesgo.evaluador.model.PersonaNatural;
import com.riesgo.evaluador.model.PersonaJuridica;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class EvaluadorRiesgoAlto extends EvaluadorRiesgo {

    @Override
    protected BigDecimal aplicarPenalizacionesAdicionales(Cliente cliente, BigDecimal puntajeBase) {
        BigDecimal puntaje = puntajeBase;
        
        // Penalizaciones severas para riesgo alto
        if (cliente instanceof PersonaNatural pn) {
            // Penalización por edad de alto riesgo
            if (pn.getEdad() < 20 || pn.getEdad() > 65) {
                puntaje = puntaje.subtract(new BigDecimal("10"));
            }
            // Penalización significativa por muchos dependientes
            if (pn.getNumeroDependientes() != null && pn.getNumeroDependientes() > 4) {
                puntaje = puntaje.subtract(new BigDecimal("8"));
            }
            // Muy pequeña bonificación para edades estables (ya que es alto riesgo)
            if (pn.getEdad() >= 30 && pn.getEdad() <= 50) {
                puntaje = puntaje.add(new BigDecimal("2"));
            }
        } else if (cliente instanceof PersonaJuridica pj) {
            // Penalización severa por empresas muy nuevas
            if (pj.getAnosConstitucion() < 2) {
                puntaje = puntaje.subtract(new BigDecimal("15"));
            }
            // Bonificación mínima por mucha experiencia
            if (pj.getAnosConstitucion() >= 10) {
                puntaje = puntaje.add(new BigDecimal("8"));
            }
            // Penalización por sectores de alto riesgo
            if ("MINERIA".equals(pj.getSectorEconomico()) || 
                "AGRICULTURA".equals(pj.getSectorEconomico()) ||
                "ENTRETENIMIENTO".equals(pj.getSectorEconomico())) {
                puntaje = puntaje.subtract(new BigDecimal("5"));
            }
            // Penalización por empresa muy pequeña
            if (pj.getNumeroEmpleados() != null && pj.getNumeroEmpleados() < 5) {
                puntaje = puntaje.subtract(new BigDecimal("3"));
            }
        }
        
        // Penalización adicional por deudas en mora
        if (cliente.getDeudas() != null) {
            long deudasMora = cliente.getDeudas().stream()
                .filter(d -> d.isEnMora())
                .count();
            puntaje = puntaje.subtract(new BigDecimal(deudasMora * 5));
        }
        
        return puntaje;
    }

    @Override
    protected BigDecimal calcularMontoMaximo(Cliente cliente, BigDecimal puntaje) {
        BigDecimal ingresos = cliente.getIngresos();
        BigDecimal multiplicador = new BigDecimal("1.5"); // Solo 1.5 veces los ingresos para riesgo alto
        
        // Ajuste muy conservador por puntaje
        if (puntaje.compareTo(new BigDecimal("50")) >= 0) {
            multiplicador = new BigDecimal("2");
        } else if (puntaje.compareTo(new BigDecimal("30")) < 0) {
            multiplicador = new BigDecimal("1");
        }
        
        return ingresos.multiply(multiplicador).setScale(2, RoundingMode.HALF_UP);
    }
}