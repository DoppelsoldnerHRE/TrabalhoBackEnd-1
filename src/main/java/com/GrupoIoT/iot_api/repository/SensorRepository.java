package com.GrupoIoT.iot_api.repository;

import com.GrupoIoT.iot_api.model.entity.Sensor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {

  // Buscar sensores de um dispositivo
  Page<Sensor> findByDispositivoId(Long dispositivoId, Pageable pageable);

  // Buscar por tipo de sensor
  Page<Sensor> findByTipoSensorContainingIgnoreCase(String tipoSensor, Pageable pageable);

  // Buscar sensores ativos
  Page<Sensor> findByAtivo(Boolean ativo, Pageable pageable);

  // Buscar sensores ativos de um dispositivo
  Page<Sensor> findByDispositivoIdAndAtivo(Long dispositivoId, Boolean ativo, Pageable pageable);

  // Buscar por nome
  Page<Sensor> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

  // 🎯 CARTA-DESAFIO INATIVOS: Sensores sem atualização há mais de 7 dias
  @Query("SELECT s FROM Sensor s WHERE s.ultimaAtualizacao < :dataLimite")
  Page<Sensor> findInativos(@Param("dataLimite") LocalDateTime dataLimite, Pageable pageable);

  // Contar sensores inativos
  @Query("SELECT COUNT(s) FROM Sensor s WHERE s.ultimaAtualizacao < :dataLimite")
  long countInativos(@Param("dataLimite") LocalDateTime dataLimite);

  // Buscar sensores inativos de um dispositivo específico
  @Query("SELECT s FROM Sensor s WHERE s.dispositivo.id = :dispositivoId AND s.ultimaAtualizacao < :dataLimite")
  Page<Sensor> findInativosByDispositivo(
    @Param("dispositivoId") Long dispositivoId,
    @Param("dataLimite") LocalDateTime dataLimite,
    Pageable pageable
  );

  // Buscar sensores inativos de um usuário
  @Query("SELECT s FROM Sensor s WHERE s.dispositivo.usuario.id = :usuarioId AND s.ultimaAtualizacao < :dataLimite")
  Page<Sensor> findInativosByUsuario(
    @Param("usuarioId") Long usuarioId,
    @Param("dataLimite") LocalDateTime dataLimite,
    Pageable pageable
  );

  // Buscar com filtros combinados
  @Query("SELECT s FROM Sensor s WHERE " +
    "(:nome IS NULL OR LOWER(s.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) AND " +
    "(:tipoSensor IS NULL OR LOWER(s.tipoSensor) LIKE LOWER(CONCAT('%', :tipoSensor, '%'))) AND " +
    "(:ativo IS NULL OR s.ativo = :ativo) AND " +
    "(:dispositivoId IS NULL OR s.dispositivo.id = :dispositivoId)")
  Page<Sensor> findWithFilters(
    @Param("nome") String nome,
    @Param("tipoSensor") String tipoSensor,
    @Param("ativo") Boolean ativo,
    @Param("dispositivoId") Long dispositivoId,
    Pageable pageable
  );

  // Listar todos os sensores (sem paginação) - útil para exportações
  List<Sensor> findByDispositivoId(Long dispositivoId);
}
