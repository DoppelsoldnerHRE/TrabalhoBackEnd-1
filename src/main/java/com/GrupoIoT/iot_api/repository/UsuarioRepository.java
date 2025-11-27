package com.GrupoIoT.iot_api.repository;

import com.GrupoIoT.iot_api.model.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

  // Buscar por email (para login e validação de unicidade)
  Optional<Usuario> findByEmail(String email);

  // Verificar se email já existe
  boolean existsByEmail(String email);

  // Buscar por nome (com paginação)
  Page<Usuario> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
