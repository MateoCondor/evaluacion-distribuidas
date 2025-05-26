package com.riesgo.evaluador.controller;

import com.riesgo.evaluador.dto.EvaluacionRequestDTO;
import com.riesgo.evaluador.dto.EvaluacionResponseDTO;
import com.riesgo.evaluador.dto.HistorialEvaluacionDTO;
import com.riesgo.evaluador.service.EvaluacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/evaluacion")
@CrossOrigin(origins = "*")
public class EvaluacionController {
    
    @Autowired
    private EvaluacionService evaluacionService;
    
    /**
     * Evaluar riesgo de un cliente
     * POST /api/evaluacion/evaluar
     */
    @PostMapping("/evaluar")
    public ResponseEntity<?> evaluarRiesgo(@RequestBody EvaluacionRequestDTO request) {
        try {
            EvaluacionResponseDTO response = evaluacionService.evaluar(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(crearRespuestaError("Error de validación", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearRespuestaError("Error interno", "Error al procesar la evaluación"));
        }
    }
    
    /**
     * Obtener todo el historial de evaluaciones
     * GET /api/evaluacion/historial
     */
    @GetMapping("/historial")
    public ResponseEntity<List<HistorialEvaluacionDTO>> obtenerHistorial() {
        try {
            List<HistorialEvaluacionDTO> historial = evaluacionService.obtenerHistorial();
            return ResponseEntity.ok(historial);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtener historial por cliente específico
     * GET /api/evaluacion/historial/cliente/{clienteId}
     */
    @GetMapping("/historial/cliente/{clienteId}")
    public ResponseEntity<List<HistorialEvaluacionDTO>> obtenerHistorialPorCliente(
            @PathVariable Long clienteId) {
        try {
            List<HistorialEvaluacionDTO> historial = evaluacionService.obtenerHistorialPorCliente(clienteId);
            return ResponseEntity.ok(historial);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtener historial por nivel de riesgo
     * GET /api/evaluacion/historial/riesgo/{nivelRiesgo}
     */
    @GetMapping("/historial/riesgo/{nivelRiesgo}")
    public ResponseEntity<List<HistorialEvaluacionDTO>> obtenerHistorialPorRiesgo(
            @PathVariable String nivelRiesgo) {
        try {
            // Validar nivel de riesgo
            if (!nivelRiesgo.equals("BAJO") && !nivelRiesgo.equals("MEDIO") && !nivelRiesgo.equals("ALTO")) {
                return ResponseEntity.badRequest().build();
            }
            
            List<HistorialEvaluacionDTO> historial = evaluacionService.obtenerHistorialPorNivelRiesgo(nivelRiesgo);
            return ResponseEntity.ok(historial);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtener historial por rango de fechas
     * GET /api/evaluacion/historial/fechas?inicio=2024-01-01T00:00:00&fin=2024-12-31T23:59:59
     */
    @GetMapping("/historial/fechas")
    public ResponseEntity<List<HistorialEvaluacionDTO>> obtenerHistorialPorFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        try {
            if (inicio.isAfter(fin)) {
                return ResponseEntity.badRequest().build();
            }
            
            List<HistorialEvaluacionDTO> historial = evaluacionService.obtenerHistorialPorFechas(inicio, fin);
            return ResponseEntity.ok(historial);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Endpoint de prueba para verificar que la API está funcionando
     * GET /api/evaluacion/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "API de Evaluación de Riesgo funcionando correctamente");
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Obtener información sobre los tipos de evaluación disponibles
     * GET /api/evaluacion/info
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> obtenerInformacion() {
        Map<String, Object> info = new HashMap<>();
        
        // Tipos de cliente
        info.put("tiposCliente", List.of("NATURAL", "JURIDICA"));
        
        // Niveles de riesgo
        info.put("nivelesRiesgo", List.of("BAJO", "MEDIO", "ALTO"));
        
        // Criterios de evaluación
        Map<String, String> criterios = new HashMap<>();
        criterios.put("BAJO", "Ratio deuda/ingreso <= 0.4 y sin moras");
        criterios.put("MEDIO", "Ratio deuda/ingreso entre 0.4 y 0.6");
        criterios.put("ALTO", "Ratio deuda/ingreso > 0.6 o tiene deudas en mora");
        info.put("criteriosEvaluacion", criterios);
        
        // Campos requeridos por tipo
        Map<String, List<String>> camposRequeridos = new HashMap<>();
        camposRequeridos.put("NATURAL", List.of("nombre", "documento", "ingresos", "fechaNacimiento"));
        camposRequeridos.put("JURIDICA", List.of("nombre", "documento", "ingresos", "fechaConstitucion"));
        info.put("camposRequeridos", camposRequeridos);
        
        return ResponseEntity.ok(info);
    }
    
    /**
     * Método helper para crear respuestas de error consistentes
     */
    private Map<String, Object> crearRespuestaError(String tipo, String mensaje) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", tipo);
        error.put("mensaje", mensaje);
        error.put("timestamp", LocalDateTime.now().toString());
        return error;
    }
}