package com.riesgo.evaluador.repository;

import com.riesgo.evaluador.model.HistorialEvaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HistorialRepository extends JpaRepository<HistorialEvaluacion, Long> {
    
    // Buscar por cliente ID
    List<HistorialEvaluacion> findByClienteIdOrderByFechaEvaluacionDesc(Long clienteId);
    
    // Buscar por nivel de riesgo
    List<HistorialEvaluacion> findByNivelRiesgoOrderByFechaEvaluacionDesc(String nivelRiesgo);
    
    // Buscar evaluaciones aprobadas
    List<HistorialEvaluacion> findByAprobadoTrueOrderByFechaEvaluacionDesc();
    
    // Buscar por rango de fechas
    @Query("SELECT h FROM HistorialEvaluacion h WHERE h.fechaEvaluacion BETWEEN :inicio AND :fin ORDER BY h.fechaEvaluacion DESC")
    List<HistorialEvaluacion> findByFechaEvaluacionBetween(
        @Param("inicio") LocalDateTime inicio, 
        @Param("fin") LocalDateTime fin
    );
    
    // Contar evaluaciones por nivel de riesgo
    @Query("SELECT h.nivelRiesgo, COUNT(h) FROM HistorialEvaluacion h GROUP BY h.nivelRiesgo")
    List<Object[]> countByNivelRiesgo();
}