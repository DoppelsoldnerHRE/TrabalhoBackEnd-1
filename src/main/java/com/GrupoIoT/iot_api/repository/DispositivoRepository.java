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


  Optional<Dispositivo> findByEnderecoMac(String enderecoMac);


  boolean existsByEnderecoMac(String enderecoMac);


  Page<Dispositivo> findByUsuarioId(Long usuarioId, Pageable pageable);


  Page<Dispositivo> findByStatus(StatusDispositivo status, Pageable pageable);


  Page<Dispositivo> findByTipoContainingIgnoreCase(String tipo, Pageable pageable);


  Page<Dispositivo> findByLocalizacaoContainingIgnoreCase(String localizacao, Pageable pageable);


  Page<Dispositivo> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

 
  @Query("SELECT d FROM Dispositivo d WHERE d.ultimaAtualizacao < :dataLimite")
  Page<Dispositivo> findInativos(@Param("dataLimite") LocalDateTime dataLimite, Pageable pageable);


  @Query("SELECT COUNT(d) FROM Dispositivo d WHERE d.ultimaAtualizacao < :dataLimite")
  long countInativos(@Param("dataLimite") LocalDateTime dataLimite);


  @Query("SELECT d FROM Dispositivo d WHERE d.usuario.id = :usuarioId AND d.ultimaAtualizacao < :dataLimite")
  Page<Dispositivo> findInativosByUsuario(
    @Param("usuarioId") Long usuarioId,
    @Param("dataLimite") LocalDateTime dataLimite,
    Pageable pageable
  );

 
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
