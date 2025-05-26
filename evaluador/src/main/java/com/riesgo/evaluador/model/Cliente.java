package com.riesgo.evaluador.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_cliente")
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Cliente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false, unique = true)
    private String documento;
    
    @Column(nullable = false)
    private BigDecimal ingresos;
    
    // Nuevos campos añadidos
    @Column(name = "monto_solicitado")
    private BigDecimal montoSolicitado;
    
    @Column(name = "plazo_meses")
    private Integer plazoEnMeses;
    
    @Column(name = "puntaje_crediticio")
    private Integer puntajeCrediticio;
    
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Deuda> deudas;
    
    // Método abstracto que implementarán las subclases
    public abstract String getTipoCliente();
    
    // Método para calcular el total de deudas
    public BigDecimal getTotalDeudas() {
        return deudas != null ? 
            deudas.stream()
                .map(Deuda::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add) : 
            BigDecimal.ZERO;
    }
    
    // Método para calcular ratio deuda/ingreso
    public BigDecimal getRatioDeudaIngreso() {
        if (ingresos.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ONE;
        }
        return getTotalDeudas().divide(ingresos, 4, BigDecimal.ROUND_HALF_UP);
    }
    
    // Método para calcular ratio monto solicitado/ingreso
    public BigDecimal getRatioMontoSolicitadoIngreso() {
        if (ingresos.compareTo(BigDecimal.ZERO) == 0 || montoSolicitado == null) {
            return BigDecimal.ZERO;
        }
        return montoSolicitado.divide(ingresos, 4, BigDecimal.ROUND_HALF_UP);
    }
}