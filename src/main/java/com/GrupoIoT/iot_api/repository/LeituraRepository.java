package com.GrupoIoT.iot_api.repository;

import com.GrupoIoT.iot_api.model.entity.Leitura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LeituraRepository extends JpaRepository<Leitura, Long> {


  Page<Leitura> findBySensorId(Long sensorId, Pageable pageable);


  Page<Leitura> findByAlerta(Boolean alerta, Pageable pageable);


  Page<Leitura> findBySensorIdAndAlerta(Long sensorId, Boolean alerta, Pageable pageable);


  @Query("SELECT l FROM Leitura l WHERE l.dataHora BETWEEN :dataInicio AND :dataFim")
  Page<Leitura> findByPeriodo(
    @Param("dataInicio") LocalDateTime dataInicio,
    @Param("dataFim") LocalDateTime dataFim,
    Pageable pageable
  );


  @Query("SELECT l FROM Leitura l WHERE l.sensor.id = :sensorId AND l.dataHora BETWEEN :dataInicio AND :dataFim")
  Page<Leitura> findBySensorIdAndPeriodo(
    @Param("sensorId") Long sensorId,
    @Param("dataInicio") LocalDateTime dataInicio,
    @Param("dataFim") LocalDateTime dataFim,
    Pageable pageable
  );


  @Query("SELECT l FROM Leitura l WHERE l.sensor.id = :sensorId ORDER BY l.dataHora DESC")
  Page<Leitura> findUltimasLeiturasBySensorId(@Param("sensorId") Long sensorId, Pageable pageable);


  @Query("SELECT l FROM Leitura l WHERE l.sensor.dispositivo.id = :dispositivoId")
  Page<Leitura> findByDispositivoId(@Param("dispositivoId") Long dispositivoId, Pageable pageable);

  @Query("SELECT AVG(l.valor) FROM Leitura l WHERE l.sensor.id = :sensorId AND l.dataHora BETWEEN :dataInicio AND :dataFim")
  Double calcularMedia(
    @Param("sensorId") Long sensorId,
    @Param("dataInicio") LocalDateTime dataInicio,
    @Param("dataFim") LocalDateTime dataFim
  );


  @Query("SELECT MIN(l.valor) FROM Leitura l WHERE l.sensor.id = :sensorId AND l.dataHora BETWEEN :dataInicio AND :dataFim")
  Double calcularMinimo(
    @Param("sensorId") Long sensorId,
    @Param("dataInicio") LocalDateTime dataInicio,
    @Param("dataFim") LocalDateTime dataFim
  );


  @Query("SELECT MAX(l.valor) FROM Leitura l WHERE l.sensor.id = :sensorId AND l.dataHora BETWEEN :dataInicio AND :dataFim")
  Double calcularMaximo(
    @Param("sensorId") Long sensorId,
    @Param("dataInicio") LocalDateTime dataInicio,
    @Param("dataFim") LocalDateTime dataFim
  );

  @Query("SELECT COUNT(l) FROM Leitura l WHERE l.sensor.id = :sensorId AND l.alerta = true AND l.dataHora BETWEEN :dataInicio AND :dataFim")
  long contarAlertas(
    @Param("sensorId") Long sensorId,
    @Param("dataInicio") LocalDateTime dataInicio,
    @Param("dataFim") LocalDateTime dataFim
  );


  @Query("SELECT COUNT(l) FROM Leitura l WHERE l.sensor.id = :sensorId AND l.dataHora BETWEEN :dataInicio AND :dataFim")
  long contarLeituras(
    @Param("sensorId") Long sensorId,
    @Param("dataInicio") LocalDateTime dataInicio,
    @Param("dataFim") LocalDateTime dataFim
  );

 
  @Query("SELECT l FROM Leitura l WHERE " +
    "(:sensorId IS NULL OR l.sensor.id = :sensorId) AND " +
    "(:alerta IS NULL OR l.alerta = :alerta) AND " +
    "(:dataInicio IS NULL OR l.dataHora >= :dataInicio) AND " +
    "(:dataFim IS NULL OR l.dataHora <= :dataFim)")
  Page<Leitura> findWithFilters(
    @Param("sensorId") Long sensorId,
    @Param("alerta") Boolean alerta,
    @Param("dataInicio") LocalDateTime dataInicio,
    @Param("dataFim") LocalDateTime dataFim,
    Pageable pageable
  );

  @Query("DELETE FROM Leitura l WHERE l.dataHora < :dataLimite")
  void deletarLeiturasAntigas(@Param("dataLimite") LocalDateTime dataLimite);

  @Query("SELECT l FROM Leitura l WHERE l.sensor.id = :sensorId ORDER BY l.dataHora DESC LIMIT 1")
  Leitura findUltimaLeituraBySensorId(@Param("sensorId") Long sensorId);
}
