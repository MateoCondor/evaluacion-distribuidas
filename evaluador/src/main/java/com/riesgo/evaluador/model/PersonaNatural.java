package com.riesgo.evaluador.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Period;

@Entity
@Table(name = "personas_naturales")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PersonaNatural extends Cliente {
    
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;
    
    @Column(name = "estado_civil")
    private String estadoCivil;
    
    @Column(name = "numero_dependientes")
    private Integer numeroDependientes;
    
    @Override
    public String getTipoCliente() {
        return "NATURAL";
    }
    
    // Método para calcular la edad
    public Integer getEdad() {
        if (fechaNacimiento == null) {
            return null;
        }
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }
}