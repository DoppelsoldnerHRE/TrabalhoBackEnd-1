package com.GrupoIoT.iot_api.repository;

import com.GrupoIoT.iot_api.model.entity.Dispositivo;
import com.GrupoIoT.iot_api.model.entity.Dispositivo.StatusDispositivo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DispositivoRepository extends JpaRepository<Dispositivo, Long> {

  // Buscar por endereço MAC (único)
  Optional<Dispositivo> findByEnderecoMac(String enderecoMac);

  // Verificar se MAC já existe
  boolean existsByEnderecoMac(String enderecoMac);

  // Buscar por usuário
  Page<Dispositivo> findByUsuarioId(Long usuarioId, Pageable pageable);

  // Buscar por status
  Page<Dispositivo> findByStatus(StatusDispositivo status, Pageable pageable);

  // Buscar por tipo
  Page<Dispositivo> findByTipoContainingIgnoreCase(String tipo, Pageable pageable);

  // Buscar por localização
  Page<Dispositivo> findByLocalizacaoContainingIgnoreCase(String localizacao, Pageable pageable);

  // Buscar por nome
  Page<Dispositivo> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

  // 🎯 CARTA-DESAFIO INATIVOS: Dispositivos sem atualização há mais de 7 dias
  @Query("SELECT d FROM Dispositivo d WHERE d.ultimaAtualizacao < :dataLimite")
  Page<Dispositivo> findInativos(@Param("dataLimite") LocalDateTime dataLimite, Pageable pageable);

  // Contar dispositivos inativos
  @Query("SELECT COUNT(d) FROM Dispositivo d WHERE d.ultimaAtualizacao < :dataLimite")
  long countInativos(@Param("dataLimite") LocalDateTime dataLimite);

  // Buscar dispositivos inativos de um usuário específico
  @Query("SELECT d FROM Dispositivo d WHERE d.usuario.id = :usuarioId AND d.ultimaAtualizacao < :dataLimite")
  Page<Dispositivo> findInativosByUsuario(
    @Param("usuarioId") Long usuarioId,
    @Param("dataLimite") LocalDateTime dataLimite,
    Pageable pageable
  );

  // Buscar com filtros combinados
  @Query("SELECT d FROM Dispositivo d WHERE " +
    "(:nome IS NULL OR LOWER(d.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) AND " +
    "(:tipo IS NULL OR LOWER(d.tipo) LIKE LOWER(CONCAT('%', :tipo, '%'))) AND " +
    "(:localizacao IS NULL OR LOWER(d.localizacao) LIKE LOWER(CONCAT('%', :localizacao, '%'))) AND " +
    "(:status IS NULL OR d.status = :status) AND " +
    "(:usuarioId IS NULL OR d.usuario.id = :usuarioId)")
  Page<Dispositivo> findWithFilters(
    @Param("nome") String nome,
    @Param("tipo") String tipo,
    @Param("localizacao") String localizacao,
    @Param("status") StatusDispositivo status,
    @Param("usuarioId") Long usuarioId,
    Pageable pageable
  );
}
