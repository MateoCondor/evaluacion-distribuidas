package com.riesgo.evaluador.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "deudas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Deuda {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String tipo;
    
    @Column(nullable = false)
    private BigDecimal monto;
    
    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;
    
    @Column(name = "dias_mora")
    private Integer diasMora = 0;
    
    @Column(name = "plazo_meses") // Nuevo campo
    private Integer plazoMeses;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
    
    // Método para verificar si la deuda está vencida
    public boolean isVencida() {
        return fechaVencimiento != null && 
               fechaVencimiento.isBefore(LocalDate.now());
    }
    
    // Método para verificar si está en mora
    public boolean isEnMora() {
        return diasMora != null && diasMora > 0;
    }
}