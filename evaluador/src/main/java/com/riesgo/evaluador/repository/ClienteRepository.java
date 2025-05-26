package com.riesgo.evaluador.repository;

import com.riesgo.evaluador.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    Optional<Cliente> findByDocumento(String documento);
    
    boolean existsByDocumento(String documento);
}