package com.riesgo.evaluador.service;

import com.riesgo.evaluador.dto.EvaluacionRequestDTO;
import com.riesgo.evaluador.dto.EvaluacionResponseDTO;
import com.riesgo.evaluador.dto.HistorialEvaluacionDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface EvaluacionService {
    
    EvaluacionResponseDTO evaluar(EvaluacionRequestDTO request);
    
    List<HistorialEvaluacionDTO> obtenerHistorial();
    
    List<HistorialEvaluacionDTO> obtenerHistorialPorCliente(Long clienteId);
    
    List<HistorialEvaluacionDTO> obtenerHistorialPorNivelRiesgo(String nivelRiesgo);
    
    List<HistorialEvaluacionDTO> obtenerHistorialPorFechas(LocalDateTime inicio, LocalDateTime fin);
}