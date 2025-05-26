package com.riesgo.evaluador.service;

import com.riesgo.evaluador.model.Cliente;
import com.riesgo.evaluador.model.PersonaNatural;
import com.riesgo.evaluador.model.PersonaJuridica;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class EvaluadorRiesgoBajo extends EvaluadorRiesgo {

    @Override
    protected BigDecimal aplicarPenalizacionesAdicionales(Cliente cliente, BigDecimal puntajeBase) {
        BigDecimal puntaje = puntajeBase;
        
        // Bonificaciones para riesgo bajo
        if (cliente instanceof PersonaNatural pn) {
            // Bonificación por edad óptima
            if (pn.getEdad() >= 25 && pn.getEdad() <= 55) {
                puntaje = puntaje.add(new BigDecimal("5"));
            }
            // Pequeña penalización por muchos dependientes
            if (pn.getNumeroDependientes() != null && pn.getNumeroDependientes() > 3) {
                puntaje = puntaje.subtract(new BigDecimal("3"));
            }
        } else if (cliente instanceof PersonaJuridica pj) {
            // Bonificación por años de constitución
            if (pj.getAnosConstitucion() >= 5) {
                puntaje = puntaje.add(new BigDecimal("10"));
            }
            // Bonificación por sector estable
            if ("TECNOLOGIA".equals(pj.getSectorEconomico()) || 
                "SERVICIOS".equals(pj.getSectorEconomico())) {
                puntaje = puntaje.add(new BigDecimal("5"));
            }
        }
        
        return puntaje;
    }

    @Override
    protected BigDecimal calcularMontoMaximo(Cliente cliente, BigDecimal puntaje) {
        BigDecimal ingresos = cliente.getIngresos();
        BigDecimal multiplicador = new BigDecimal("5"); // 5 veces los ingresos para riesgo bajo
        
        // Multiplicador adicional por puntaje alto
        if (puntaje.compareTo(new BigDecimal("90")) >= 0) {
            multiplicador = new BigDecimal("6");
        }
        
        return ingresos.multiply(multiplicador).setScale(2, RoundingMode.HALF_UP);
    }
}