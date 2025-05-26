package com.riesgo.evaluador.service;

import com.riesgo.evaluador.dto.EvaluacionRequestDTO;
import com.riesgo.evaluador.dto.EvaluacionResponseDTO;
import com.riesgo.evaluador.dto.HistorialEvaluacionDTO;
import com.riesgo.evaluador.model.*;
import com.riesgo.evaluador.repository.ClienteRepository;
import com.riesgo.evaluador.repository.HistorialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EvaluacionServiceImpl implements EvaluacionService {
    
    @Autowired
    private EvaluadorRiesgoBajo evaluadorBajo;
    
    @Autowired
    private EvaluadorRiesgoMedio evaluadorMedio;
    
    @Autowired
    private EvaluadorRiesgoAlto evaluadorAlto;
    
    @Autowired
    private HistorialRepository historialRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Override
    public EvaluacionResponseDTO evaluar(EvaluacionRequestDTO request) {
        try {
            System.out.println("=== INICIO EVALUACIÓN ===");
            System.out.println("Request recibido para: " + request.getNombre());
            
            // 1. Validar datos de entrada
            System.out.println("1. Validando request...");
            validarRequest(request);
            System.out.println("✅ Validación exitosa");
            
            // 2. Crear el cliente según el tipo
            System.out.println("2. Creando cliente...");
            Cliente cliente = crearCliente(request);
            System.out.println("✅ Cliente creado: " + cliente.getTipoCliente());
            System.out.println("   - Ingresos: " + cliente.getIngresos());
            System.out.println("   - Monto solicitado: " + cliente.getMontoSolicitado());
            
            // 2.5. GUARDAR EL CLIENTE EN LA BASE DE DATOS
            System.out.println("2.5. Guardando cliente en BD...");
            cliente = clienteRepository.save(cliente);
            System.out.println("✅ Cliente guardado con ID: " + cliente.getId());
            
            // 3. Seleccionar el evaluador apropiado
            System.out.println("3. Seleccionando evaluador...");
            EvaluadorRiesgo evaluador = seleccionarEvaluador(cliente);
            System.out.println("✅ Evaluador seleccionado: " + evaluador.getClass().getSimpleName());
            
            // 4. Realizar la evaluación
            System.out.println("4. Iniciando evaluación...");
            ResultadoEvaluacion resultado = evaluador.evaluar(cliente);
            System.out.println("✅ Evaluación completada:");
            System.out.println("   - Nivel: " + resultado.getNivelRiesgo());
            System.out.println("   - Puntaje: " + resultado.getPuntaje());
            System.out.println("   - Aprobado: " + resultado.isAprobado());
            
            // 5. Guardar en el historial
            System.out.println("5. Guardando historial...");
            HistorialEvaluacion historial = HistorialEvaluacion.fromResultado(
                cliente.getId(), 
                cliente.getTipoCliente(), 
                resultado,
                cliente.getIngresos(),
                cliente.getTotalDeudas()
            );
            historialRepository.save(historial);
            System.out.println("✅ Historial guardado");
            
            // 6. Retornar respuesta
            System.out.println("6. Creando respuesta...");
            EvaluacionResponseDTO response = EvaluacionResponseDTO.fromEvaluacion(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getDocumento(),
                cliente.getTipoCliente(),
                resultado,
                cliente.getIngresos(),
                cliente.getTotalDeudas(),
                cliente.getMontoSolicitado(),
                cliente.getPlazoEnMeses(),
                cliente.getPuntajeCrediticio()
            );
            System.out.println("✅ Respuesta creada exitosamente");
            System.out.println("=== FIN EVALUACIÓN ===");
            
            return response;
            
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Error de validación: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            System.err.println("❌ ERROR INTERNO EN EVALUACIÓN: " + e.getMessage());
            System.err.println("Tipo de excepción: " + e.getClass().getName());
            e.printStackTrace();
            throw new RuntimeException("Error al procesar la evaluación", e);
        }
    }
    
    private void validarRequest(EvaluacionRequestDTO request) {
        if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        
        if (request.getDocumento() == null || request.getDocumento().trim().isEmpty()) {
            throw new IllegalArgumentException("El documento es obligatorio");
        }
        
        if (request.getIngresos() == null || request.getIngresos().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Los ingresos son obligatorios y deben ser mayor a 0");
        }
        
        if (request.getTipoCliente() == null || 
            (!request.getTipoCliente().equals("NATURAL") && !request.getTipoCliente().equals("JURIDICA"))) {
            throw new IllegalArgumentException("Tipo de cliente debe ser NATURAL o JURIDICA");
        }
        
        // Validar monto solicitado
        if (request.getMontoSolicitado() == null || request.getMontoSolicitado().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto solicitado es obligatorio y debe ser mayor a 0");
        }
        
        // Validar plazo
        if (request.getPlazoEnMeses() == null || request.getPlazoEnMeses() <= 0) {
            throw new IllegalArgumentException("El plazo en meses es obligatorio y debe ser mayor a 0");
        }
        
        // Validaciones específicas por tipo
        if ("NATURAL".equals(request.getTipoCliente())) {
            if (request.getFechaNacimiento() == null && request.getEdad() == null) {
                throw new IllegalArgumentException("La fecha de nacimiento o edad es obligatoria para personas naturales");
            }
        }
        
        if ("JURIDICA".equals(request.getTipoCliente()) && request.getFechaConstitucion() == null) {
            throw new IllegalArgumentException("La fecha de constitución es obligatoria para personas jurídicas");
        }
        
        // Validar deudas si existen
        if (request.getDeudas() != null) {
            for (EvaluacionRequestDTO.DeudaDTO deuda : request.getDeudas()) {
                if (deuda.getTipo() == null || deuda.getTipo().trim().isEmpty()) {
                    throw new IllegalArgumentException("El tipo de deuda es obligatorio");
                }
                if (deuda.getMonto() == null || deuda.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("El monto de la deuda debe ser mayor a 0");
                }
                if (deuda.getDiasMora() != null && deuda.getDiasMora() < 0) {
                    throw new IllegalArgumentException("Los días de mora no pueden ser negativos");
                }
            }
        }
    }
    
    private Cliente crearCliente(EvaluacionRequestDTO request) {
        Cliente cliente;
        
        if ("NATURAL".equals(request.getTipoCliente())) {
            PersonaNatural pn = new PersonaNatural();
            pn.setFechaNacimiento(request.getFechaNacimiento());
            pn.setEstadoCivil(request.getEstadoCivil());
            pn.setNumeroDependientes(request.getNumeroDependientes() != null ? request.getNumeroDependientes() : 0);
            
            // Si no hay fecha de nacimiento pero sí edad, usar la edad directamente
            if (request.getFechaNacimiento() == null && request.getEdad() != null) {
                // Calcular fecha aproximada basada en la edad
                pn.setFechaNacimiento(java.time.LocalDate.now().minusYears(request.getEdad()));
            }
            
            cliente = pn;
        } else {
            PersonaJuridica pj = new PersonaJuridica();
            pj.setFechaConstitucion(request.getFechaConstitucion());
            pj.setSectorEconomico(request.getSectorEconomico());
            pj.setNumeroEmpleados(request.getNumeroEmpleados() != null ? request.getNumeroEmpleados() : 1);
            cliente = pj;
        }
        
        // Datos comunes
        cliente.setNombre(request.getNombre());
        cliente.setDocumento(request.getDocumento());
        cliente.setIngresos(request.getIngresos());
        cliente.setMontoSolicitado(request.getMontoSolicitado());
        cliente.setPlazoEnMeses(request.getPlazoEnMeses());
        cliente.setPuntajeCrediticio(request.getPuntajeCrediticio());
        
        // Crear deudas
        if (request.getDeudas() != null && !request.getDeudas().isEmpty()) {
            List<Deuda> deudas = request.getDeudas().stream()
                .map(this::crearDeuda)
                .collect(Collectors.toList());
            deudas.forEach(deuda -> deuda.setCliente(cliente));
            cliente.setDeudas(deudas);
        } else {
            cliente.setDeudas(List.of()); // Lista vacía si no tiene deudas
        }
        
        return cliente;
    }
    
    private Deuda crearDeuda(EvaluacionRequestDTO.DeudaDTO deudaDTO) {
        Deuda deuda = new Deuda();
        deuda.setTipo(deudaDTO.getTipo());
        deuda.setMonto(deudaDTO.getMonto());
        deuda.setFechaVencimiento(deudaDTO.getFechaVencimiento());
        deuda.setDiasMora(deudaDTO.getDiasMora() != null ? deudaDTO.getDiasMora() : 0);
        deuda.setPlazoMeses(deudaDTO.getPlazoMeses());
        return deuda;
    }
    
    private EvaluadorRiesgo seleccionarEvaluador(Cliente cliente) {
        BigDecimal ratioDeuda = calcularRatioDeuda(cliente);
        boolean tieneMoras = cliente.getDeudas() != null && 
            cliente.getDeudas().stream().anyMatch(d -> d.isEnMora());
        
        // Lógica de selección del evaluador
        if (tieneMoras || ratioDeuda.compareTo(new BigDecimal("0.6")) > 0) {
            return evaluadorAlto;
        } else if (ratioDeuda.compareTo(new BigDecimal("0.4")) > 0) {
            return evaluadorMedio;
        } else {
            return evaluadorBajo;
        }
    }
    
    private BigDecimal calcularRatioDeuda(Cliente cliente) {
        if (cliente.getIngresos().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ONE;
        }
        return cliente.getTotalDeudas()
            .divide(cliente.getIngresos(), 4, BigDecimal.ROUND_HALF_UP);
    }
    
    // ... resto de métodos del servicio (obtenerHistorial, etc.)
    @Override
    @Transactional(readOnly = true)
    public List<HistorialEvaluacionDTO> obtenerHistorial() {
        return historialRepository.findAll().stream()
            .map(HistorialEvaluacionDTO::fromEntity)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<HistorialEvaluacionDTO> obtenerHistorialPorCliente(Long clienteId) {
        return historialRepository.findByClienteIdOrderByFechaEvaluacionDesc(clienteId).stream()
            .map(HistorialEvaluacionDTO::fromEntity)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<HistorialEvaluacionDTO> obtenerHistorialPorNivelRiesgo(String nivelRiesgo) {
        return historialRepository.findByNivelRiesgoOrderByFechaEvaluacionDesc(nivelRiesgo).stream()
            .map(HistorialEvaluacionDTO::fromEntity)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<HistorialEvaluacionDTO> obtenerHistorialPorFechas(LocalDateTime inicio, LocalDateTime fin) {
        return historialRepository.findByFechaEvaluacionBetween(inicio, fin).stream()
            .map(HistorialEvaluacionDTO::fromEntity)
            .collect(Collectors.toList());
    }
}