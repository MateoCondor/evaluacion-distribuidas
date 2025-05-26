package com.riesgo.evaluador.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Period;

@Entity
@Table(name = "personas_juridicas")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PersonaJuridica extends Cliente {
    
    @Column(name = "fecha_constitucion")
    private LocalDate fechaConstitucion;
    
    @Column(name = "sector_economico")
    private String sectorEconomico;
    
    @Column(name = "numero_empleados")
    private Integer numeroEmpleados;
    
    @Override
    public String getTipoCliente() {
        return "JURIDICA";
    }
    
    // Método para calcular años de constitución
    public Integer getAnosConstitucion() {
        if (fechaConstitucion == null) {
            return 0;
        }
        return Period.between(fechaConstitucion, LocalDate.now()).getYears();
    }
}