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


  Page<Sensor> findByDispositivoId(Long dispositivoId, Pageable pageable);


  Page<Sensor> findByTipoSensorContainingIgnoreCase(String tipoSensor, Pageable pageable);


  Page<Sensor> findByAtivo(Boolean ativo, Pageable pageable);


  Page<Sensor> findByDispositivoIdAndAtivo(Long dispositivoId, Boolean ativo, Pageable pageable);


  Page<Sensor> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

 
  @Query("SELECT s FROM Sensor s WHERE s.ultimaAtualizacao < :dataLimite")
  Page<Sensor> findInativos(@Param("dataLimite") LocalDateTime dataLimite, Pageable pageable);


  @Query("SELECT COUNT(s) FROM Sensor s WHERE s.ultimaAtualizacao < :dataLimite")
  long countInativos(@Param("dataLimite") LocalDateTime dataLimite);


  @Query("SELECT s FROM Sensor s WHERE s.dispositivo.id = :dispositivoId AND s.ultimaAtualizacao < :dataLimite")
  Page<Sensor> findInativosByDispositivo(
    @Param("dispositivoId") Long dispositivoId,
    @Param("dataLimite") LocalDateTime dataLimite,
    Pageable pageable
  );


  @Query("SELECT s FROM Sensor s WHERE s.dispositivo.usuario.id = :usuarioId AND s.ultimaAtualizacao < :dataLimite")
  Page<Sensor> findInativosByUsuario(
    @Param("usuarioId") Long usuarioId,
    @Param("dataLimite") LocalDateTime dataLimite,
    Pageable pageable
  );


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

  List<Sensor> findByDispositivoId(Long dispositivoId);
}
